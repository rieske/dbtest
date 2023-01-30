# Fast Database Tests

Demonstrates how you can greatly speed up your integration tests against a containerized database.
Everyone knows about `fsync` and that's the default setting in testcontainers already, which is 
a huge gain for the testing speed by keeping all the changes in-memory. 
But we can do even better on top of that.

While working on a project, I noticed the database tests getting slower and slower over time.
And the culprit of that was the increasing number of database schema migrations as the project evolved.

Normally, each test wants to work with a fresh database and the easiest way to 
achieve this is to create a fresh database with all the schema migrations applied.
Applying all the migrations before every single test is where we can optimize.

## Postgres

With Postgres database, we can apply all the migrations once to the default `postgres` schema.
We can then use this database as a [template](https://www.postgresql.org/docs/current/manage-ag-templatedbs.html)
to cheaply copy the fresh state to a new database for each test.