**QuotaSentry Application**

**Description**

QuotaSentry Application is a Java-based web application that manages user data and quotas. 
It is designed to be used by administrators to create, update, delete, and synchronize user data between two databases. 
Additionally, it includes an in-memory repository that simulates an Elastic database.

**Technologies Used**

Java: A high-level programming language that supports object-oriented, concurrent, and functional programming.

Spring Boot: A Java-based framework used to create microservices and standalone applications.

Spring Data JPA: Not Used!

MySQL: An open-source relational database management system.

ElasticSearch (in-memory simulation): An in-memory data store designed to simulate the behavior of a real Elastic database.

**Features**

Create User: Create a new user with specific attributes such as ID, first name, last name, login time, etc.

Update User: Update an existing user's information, including their name, login time, and requests.

Delete User: Soft delete a user, marking them as "deleted" in the database without removing their data.

Consume Quota: Increment the number of requests made by a user and update their last login time.

Synchronize Databases: Synchronize data between two databases, using an administrator-only API.

Seed Data: Populate databases with initial data.
