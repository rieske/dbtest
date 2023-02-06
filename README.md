# Fast Database Tests

[![Actions Status](https://github.com/rieske/java-fast-database-tests/workflows/main/badge.svg)](https://github.com/rieske/java-fast-database-tests/actions)

Demonstrates how you can greatly speed up your integration tests against a containerized database.

Everyone knows about tmpfs - mounting database data directory there provides
a huge gain for the testing speed by keeping all the changes in-memory.
But we can do even better on top of that.

While working on a project, I noticed the database tests getting slower and slower over time.
And the culprit of that was the increasing number of database schema migrations as the project evolved.

Normally, each test wants to work with a fresh database and the easiest way to
achieve this is to create a fresh database with all the schema migrations applied.
Applying all the migrations before every single test is where we can optimize.

## Postgres

Postgres container from testcontainers library by default disables `fsync` which provides similar speed
boost as tmpfs by not flushing the changes to disk and keeping everything in memory.
Still, my experiments show that both `fsync off` and `tmpfs` speed things up even a tiny bit more.

To solve the migrations problem with Postgres database, we can apply all the migrations once to the default `postgres` schema.
We can then use this database as a [template](https://www.postgresql.org/docs/current/manage-ag-templatedbs.html)
to cheaply copy the fresh state to a new database for each test.

## MySql

MySql does not have built in way to copy a database from a template like Postgres.
We can work around this by applying the migrations to a database once,
then dumping it to a sql file and ingesting this file in a new database each time.
This way, we apply only a single migration that represents the latest state per test run
instead of applying all migrations every time.
