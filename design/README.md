# SkyblockEngineeringFirmaCraft


# TFC Skyblock Design Database

A small PostgreSQL + Liquibase database for designing the TFC Skyblock resource, technology, and progression graphs.

Start PostgreSQL

```bash
docker compose up -d
```

Apply the schema

```bash
liquibase   --defaultsFile=liquibase.properties   update
```

To inspect the SQL Liquibase would execute:

```bash
liquibase   --defaultsFile=liquibase.properties   update-sql
```

The database separates:

- **resources** — things that exist or are consumed
- **sources** — ways resources enter the system
- **processes** — transformations of resources
- **technologies** — capabilities available to the player
- **progression goals** — intended player-facing milestones
