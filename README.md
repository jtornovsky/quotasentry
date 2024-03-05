**QuotaSentry Application**

**Description**

QuotaSentry Application is a Java-based web application that manages user data and quotas. 
It is designed to be used by administrators to create, update, delete, and synchronize user data between two databases. 
Additionally, it includes an in-memory repository that simulates an Elastic database.


**Technologies Used**

Java: A high-level programming language that supports object-oriented, concurrent, and functional programming.

Spring Boot: A Java-based framework used to create applications.

Spring Data JPA: Not Used!

MySQL: A relational database management system.

ElasticSearch (in-memory simulation): An in-memory data store designed to simulate the behavior of a real Elastic database.

H2 Database (for testing)

JUnit (for unit testing)

Gradle (for dependency management and building)


**Architecture**

The application follows a layered architecture with clear separation of concerns:

Controller Layer: Handles incoming HTTP requests and routes them to the appropriate service.

Service Layer: Contains business logic and orchestrates interactions between the controllers and repositories.

Repository Layer: Interfaces with the database to perform CRUD operations.

Model Layer: Defines the data entities used by the application.


**Features**

Create User: Create a new user with specific attributes such as ID, first name, last name, login time, etc.

Update User: Update an existing user's information, including their name, login time, and requests.

Delete User: Soft delete a user, marking them as "deleted" in the database without removing their data.

Consume Quota: Increment the number of requests made by a user and update their last login time.

Synchronize Databases: Synchronize data between two databases, using an administrator-only API.

Seed Data: Populate databases with initial data.


**Usage**

Once the application is up and running, you can interact with it using HTTP requests or through the provided user interface (if available). 

Refer to the API documentation or user guide for detailed instructions on using the various endpoints and functionalities.

Once the application is up and running, you can interact with it using HTTP requests or through the provided user interface (if available). 

Before using the application, ensure that you seed initial data into the database. 
This can typically be done by running a specific endpoint to populate the databases with sample data. 
Refer to the java docs for instructions on seeding data.

After seeding the initial data, you can perform various operations such as creating, updating, deleting users, and consuming quotas. 
Make sure to follow the API java docs for detailed instructions on using the various endpoints and functionalities.
