# Blog REST API

This is a Java Spring Boot project that provides a REST API for a blog application. The project is structured as follows:



## Features

- User authentication and authorization
- CRUD operations for blog posts
- Commenting on blog posts
- Email notifications

## Requirements

- Java 11 or higher
- Maven
- MySQL

## Getting Started

1. Clone the repository:

```
git clone https://github.com/abdelrhmankadry/blog.git
```

2. Navigate to the project directory:

```
cd blog-rest-api
```

3. Update the `application.properties` file with your database credentials and email configuration.

4. Build the project:

```
mvn clean install
```

5. Run the application:

```
mvn spring-boot:run
```
## GitHub Actions

This project includes a GitHub Actions workflow for continuous integration. The workflow is defined in the `.github/workflows/ci.yml`. This workflow is triggered on push or pull request events for the main branch and can also be run manually from the Actions tab. It uploads code coverage reports to Codecov using the `codecov/codecov-action@v1` action.

The REST API will be available at `http://localhost:8080`.

