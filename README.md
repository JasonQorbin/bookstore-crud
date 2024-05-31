# Book store stock database CRUD app

A simple CRUD app written in Java that simulates a simple bookstore stock control system.
Provides functions to add/modify and delete book records which consist simply of a Title,
Author and quantity.

The project is built to connect to a MYSQL database but could easily be configured to use
MariaDB also.

This was a bootcamp exercise that I'm keeping to serve as a model for CRUD appps I want to make
in the future. I also hope this can serve as a nice instructionaal example to others.

## How to run the app

1. **Docker Compose**: The recommended way to run the app is using the included docker 
   compose file. This will create a MySQL database that already has a user created with privileges 
   to modify the  target database. Simply run the following command from the project root:

```console
docker compose up -d
docker attach bookstore-app
```
*Note: If you have a local MySQL/MariaDB server running then the database container won't be
able to bind to the default port (3306) and the database won't be able to start. Stop your local
database instance before trying to create the container.*

When done, remove the containers using:

```console
docker compose down
```
2. **Run with a custom database server**: To run the program with a custom database, 
   ensure that the server has a  user that the program can use that has all privileges
   over the intended database. Then modify the `database_local.ini` file with the
   details of your database and use the `run-local.bat`  file to start the program.

3. **Enter database details manually**: Run program from the command line using the 
   command from the `run.bat` or `run-local.bat` files but replace the `-i database.ini`
   arguments with `-c`. You can then enter the database details manually when the program
   starts.

