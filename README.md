# Fast Database Tests

![Maven Central](https://img.shields.io/maven-central/v/io.github.rieske.dbtest/postgresql)
[![Actions Status](https://github.com/rieske/java-fast-database-tests/workflows/main/badge.svg)](https://github.com/rieske/java-fast-database-tests/actions)

Java library that enables fast integration tests against a containerized database.

## Usage

This library exposes a JUnit5 test extension for the database vendors listed below.
[Testcontainers](https://www.testcontainers.org/) library is used under the hood to manage the Docker containers.

The extension can operate in one of three [modes](base/src/main/java/io/github/rieske/dbtest/extension/DatabaseTestExtension.java):
- `DATABASE_PER_TEST_METHOD` - Each test method gets a clean migrated database. No state leaks between tests.
  This mode is the slowest of all, but it provides the strictest state guarantees. This is what this library
  optimizes for. Scroll below to see the performance comparisons.
- `DATABASE_PER_TEST_CLASS` - Test methods within a test class will share a database.
- `DATABASE_PER_EXECUTION` - Single database will be shared by all extensions registered with this mode.
  This mode is the fastest of all, however, it provides no state guarantees. Tests must not make any
  assumptions about the state of the database (which may be modified by other tests).

### PostgreSQL

Maven Central coordinates: [`io.github.rieske.dbtest:postgresql`](https://mvnrepository.com/artifact/io.github.rieske.dbtest/postgresql)

Extend the [PostgreSQLFastTestExtension](postgresql/src/main/java/io/github/rieske/dbtest/extension/PostgreSQLFastTestExtension.java):
```java
import io.github.rieske.dbtest.extension.PostgreSQLFastTestExtension;

public class MyDatabaseTestExtension extends PostgreSQLFastTestExtension {

    public MyDatabaseTestExtension() {
        super(
              "14.4-alpine", // the Docker image tag of the official PostgreSQL Docker image
              Mode.DATABASE_PER_TEST_METHOD
        );
    }

    @Override
    protected void migrateDatabase(DataSource dataSource) {
        // Apply the database migrations using the migration tool of your choice
        // Flyway below is just an example (and this library does not bring Flyway in)
        org.flywaydb.core.Flyway.configure().dataSource(dataSource).load().migrate();
    }
}
```

Register the extension and use the data source in your tests:
```java
import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.Test;

class MyDatabaseTest {
    @RegisterExtension
    private final DatabaseTestExtension database = new MyDatabaseTestExtension(); 
    
    @Test
    void useDatabase() {
        DataSource dataSource = database.getDataSource();
        // use the database!
    }
}
```

### MySQL

Maven Central coordinates: [`io.github.rieske.dbtest:mysql`](https://mvnrepository.com/artifact/io.github.rieske.dbtest/mysql)

Usage is the same as with PostgreSQL example above, just that the base class to extend is
[MySQLTestExtension](mysql/src/main/java/io/github/rieske/dbtest/extension/MySQLTestExtension.java).

### H2

Maven Central coordinates: [`io.github.rieske.dbtest:h2`](https://mvnrepository.com/artifact/io.github.rieske.dbtest/h2)

You can also use this library with H2 database. There will be no performance gains over manual H2 usage - this library
will run the migrations for each test. However, it observes the data consistency guarantees as specified by the Mode enum
described above.

H2 is still a lot faster than a containerized database and if your setup does not require any of the database vendor
specific features that H2 does not support, you may want to develop and quickly iterate with H2 backed tests and
have the same tests executed against a containerized database during a separate test phase.

H2 version supports both PostgreSQL and MySQL compatibility modes - see [H2Mode](h2/src/main/java/io/github/rieske/dbtest/extension/H2Mode.java) enum.

## Details

We always want to test the database interactions against a database - we must know that the SQL that we wrote
(or some framework has generated for us) works and does what we expect it to do.

Before [Docker](https://www.docker.com/) and before libraries like [Testcontainers](https://www.testcontainers.org/) 
that provide convenient APIs to use containers in our  tests, the solution that somewhat works has been the 
in memory databases like [H2](https://www.h2database.com/html/main.html) or [HSQLDB](https://hsqldb.org/). 
Those are fast, they have modes to somewhat emulate some popular databases like PostgreSQL or MySQL . 
They may work fine if you don't happen to use some database vendor feature or extension
that the in-memory databases do not emulate. 
But they are still not the real thing - the tests run against one database type and 
the deployed code runs against something else.

Spawning a real database in a Docker container, however, is much slower than using the in-memory alternatives.
There are a couple of well-known tricks to make them faster:
- mount the database's data directory to `tmpfs` - this keeps all the data in-memory, which 
   results in a significant increase in execution speed.
- disable `fsync` - this function flushes the buffers to disk to ensure durability in case of system crashes.
  We don't care about data durability in our tests - all the data is ephemeral and is meant to be thrown away
  after the test finishes (or if the system crashes).

Those optimizations are always great to have. But we can do even better on top of that.

While working on a project, I noticed the database tests getting slower and slower over time.
And the culprit of that was the increasing number of database schema migrations as the project evolved.

The fastest alternative to this is to share one database with all the tests. Apply the migrations once to
initialize the database, then run all the tests against that database. The tests must then be resilient to
the database being dirtied by other tests. No assumptions can be made about contents that the given test did not
produce. This may or may not be an acceptable situation.

The original problem that this library attempts to solve is the opposite - each database integration test
assumes that it is working with a clean database. The performance bottleneck in this scenario is running all the
migrations to initialize a clean database for each test.
Solving this bottleneck is database vendor specific and works better with some than the others.

### PostgreSQL

PostgreSQL container from testcontainers library by default disables `fsync` which provides similar speed
boost as tmpfs by not flushing the changes to disk and keeping everything in memory.
Still, my experiments show that both `fsync off` and `tmpfs` speed things up even a tiny bit more.

To solve the migrations problem with PostgreSQL database, we can apply all the migrations once to the default 
`postgres` database.
We can then use this database as a [template](https://www.postgresql.org/docs/current/manage-ag-templatedbs.html)
to cheaply copy the fresh state to a new database for each test.

### MySQL

MySQL does not have built in way to copy a database from a template like PostgreSQL.
We can work around this by applying the migrations to a database once,
then dumping it to a sql file and ingesting this file in a new database each time.
This way, we apply only a single migration that represents the latest state per test run
instead of applying all migrations every time.

## Performance

To demonstrate the gains, I created [50 database migrations](base/src/testFixtures/resources/db/migration)
that perform some simple back and forth table modifications 
(in real life, the schemas are usually more complex and thus take more time to execute) and used [three simple test cases](base/src/testFixtures/java/io/github/rieske/dbtest/PerformanceTests.java):
- `doNothing` - a test that does nothing at all itself. Only the setup code runs. 
- `initDatabaseOnly` - a test that explicitly requests a data source and does nothing else.
  The data source must be ready to use and the database must be prepared/migrated.
- `interactWithDatabase` - a test that requests a data source and interacts with the database - performs an insert and a select.

I ran the tests repeatedly 1000 times each in data consistency mode both with the "migrate only once" feature and without it.

As mentioned above, this library optimizes for the `DATABASE_PER_TEST_METHOD` use case - a fresh database for each test method - 
eliminating repeated data migrations is where the gains come from.

The `DATABASE_PER_EXECUTION` mode yields the same results - the data is migrated once anyway.

The `DATABASE_PER_TEST_CLASS` mode also yields the same in the given test setup, given there is just one test class.
In the real world, we would see an improvement depending on the amount of test classes that interact with the database.

### PostgreSQL

| Test                 | migrate each time | migrate once | improvement |
|----------------------|:-----------------:|:------------:|:-----------:|
| doNothing            |       0.5s        |     0.5s     |     0%      |
| initDatabaseOnly     |      10m 56s      |    1m 31s    |   87.65%    |
| interactWithDatabase |      12m 31s      |    2m 43s    |   78.58%    |

### MySQL

| Test                 | migrate each time | migrate once | improvement |
|----------------------|:-----------------:|:------------:|:-----------:|
| doNothing            |       0.5s        |     0.5s     |     0%      |
| initDatabaseOnly     |      51m 36s      |   15m 53s    |   69.22%    |
| interactWithDatabase |      55m 2s       |   16m 26s    |   70.14%    |

I still haven't figured why MySQL performs so much worse than PostgreSQL here.
Still, the gains of saving on repeated migrations are significant.
