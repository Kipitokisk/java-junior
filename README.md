# Spring Boot Framework - Internship tasks

This repository contains a base project for internship program at GlobalDatabase, each candidate for position of
Junior Backend Developer must be able to develop all the tasks in the following lists.
Fork it and bring it to the next level.

### Workflow

1. Before starting this program you should know Java or another programming language at a level beginner, and understand
   syntax, variables, functions, algorithms, etc. If you have lacked in language understanding then start
   with https://www.learnjavaonline.org/
2. First milestone is to prove that you are ready for internship program
3. Before each milestone estimate each task in hours of work to understand how much time you need to achieve the finish,
   don't worry if your estimations are wrong because you don't have experience.
4. After each milestone or sequence of tasks create a merge request to your mentor to receive feedback, after code
   review you will have something to fix this will improve your coding skills
5. Be focused on tasks and ask questions when you don't understand something and you didn't find anything on Google (
   googling is a base skill for developers)

#### References

1. https://spring.io/ - official documentation of Spring framework
2. https://mybatis.org/spring/boot.html - documentation to mybatis integration with Spring Boot
3. https://restfulapi.net/ - documentation about REST APIs communication standard
4. https://swagger.io/docs/specification/2-0/what-is-swagger/
5. https://www.liquibase.org/get-started/quickstart - documentation about Liquibase
6. https://www.postgresqltutorial.com/ - tutorial about PostgreSQL

#### Project requirements

* [Java 11](https://docs.oracle.com/en/java/javase/11/)
* [Spring Boot 3.0.0](https://spring.io/projects/spring-boot)

### First steps to run the project

Some steps before start work on tasks.

1. Install java 11
2. Install PostgreSQL 14 and create database with name `marketplace`
3. Start the project with `mvn clean install -DskipTests` command
4. Let's do first milestone!

### Milestone 1

We start with some changes to understand the project code

#### What will you learn

1. How to run the project
2. How to create a new endpoint
3. How to create a new service
4. How to create a new repository (mapper)
5. How to create a new model

### Tasks

1. Add the ability to add a product to the database `POST /products`.
2. Add the ability to get a product from the database `GET /products/{id}`.
3. Add the ability to edit a product in the database `PUT /products/{id}`.
4. Add the ability to delete a product from the database `DELETE /products/{id}`.
5. Add the ability to get all products paginated from the database. `GET /products?page=1&page_size=10`.
6. Add validation for all endpoints.

### Milestone 2

You must work with spring security and liquibase

#### What will you learn

1. Work with database migrations
2. Work with spring security

### Tasks

1. Add the login and register functionality of a userEntity using `spring security` basic Auth.
2. Add validation for userEntity registration or auth.
3. Modify the previously created endpoints so that you cannot add a product if you are not authenticated

### Milestone 3

Working with algorithms and data structures

#### What will you learn

1. Work with spring mybatis
2. Work with spring data
3. Work with algorithms and data structures

### Tasks

1. Add the ability to search for products by name
2. Add the ability to like a product - the userEntity can like a product only once
3. Add the ability to dislike a product - the userEntity can dislike a product only once

### Milestone 4

Working with Documentation

#### What will you learn

Work with swagger

### Tasks

Integrated swagger in the data project.

### Milestone 5

[Work with COPY Postgresql](https://www.postgresql.org/docs/current/sql-copy.html#:~:text=COPY%20moves%20data%20between%20PostgreSQL,results%20of%20a%20SELECT%20query.)

### Tasks

Add a `POST /loading/products` endpoint which will get the address where the products file is located. The address to the file can be a local one or an external server via the HTTP protocol. A sample file in resources/products.csv. All these products must be assigned to the admin userEntity, in case he is not in the base then create the admin userEntity and then add these products to him.

### Milestone 6

[Work with testContainers](https://www.testcontainers.org/)

### Tasks
Create integration tests for each endpoint created using testcontainers.

## Congratulations!

Congratulations on your job well done. We appreciate your dedication, and we look forward to seeing your career
flourish.
