# Anythink Market Backend

# How it works

The application uses Spring Boot (Web, Mybatis).

And the code is organized as this:

1. `api` is the web layer implemented by Spring MVC
2. `core` is the business model including entities and services
3. `application` is the high-level services for querying the data transfer objects
4. `infrastructure`  contains all the implementation classes as the technique details

# Getting started

You'll need Java 21 installed.

    ./gradlew bootRun

To test that it works, open a browser tab at http://localhost:3000/api/tags
Alternatively, you can run:

    curl http://localhost:3000/api/tags

# Run test

The repository contains a lot of test cases to cover both api test and repository test.

    ./gradlew test

# Code format

Use spotless for code format.

    ./gradlew spotlessJavaApply
