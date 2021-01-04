
# Banking Rest API
Webservice application which expose REST API services for basic features like:
- creating customers
- creating multiple accounts
- transferring money between accounts
## Development stack 
Following REST principles, this API is implemented on the next technology stack:

- Java 11
- SpringBoot
- Spring Data
- JPA/Hibernate
- MySql DB
- Docker
- Swagger
- Gradle



## How to run it locally:

**The application uses a standalone MySql DB via docker mySql-test-constainers.**
This setup is used for running the app locally and also for running the integration tests.

Start the application and its related db:

- ```Run main class com.gohenry.bank.BankApplication```

or

-  Just type  ```./gradlew run```

##API Documentation and Playground

After the application is up and running you can find the documentation at next location:

- [Swagger User Interface](http://localhost:8084/swagger-ui.html#)






