# Mashreq Bank Coding Test

## Conference Room Booking REST API

This is a Java Spring Boot application, written as part of a coding test for Mashreq Bank. The project is a REST API
built to allow users to book meeting rooms within a company. It provides functionality to book one of four available
conference rooms based on the number of attendees and the specified time. The API ensures optimal room allocation and
adheres to the company's maintenance schedule.

## Contents

- [Conference Room Booking REST API](#conference-room-booking-rest-api)
- [Requirements](#requirements)
- [Installation and Setup](#installation-and-setup)
    - [Common Installation](#common-installation)
        - [Clone the repository](#clone-the-repository)
        - [Navigate to the project directory](#navigate-to-the-project-directory)
    - [Create Database](#create-database)
        - [Option 1: Using Docker Compose](#option-1-using-docker-compose)
        - [Option 2: Manual Setup](#option-2-manual-setup)
    - [Environment Configuration](#environment-configuration)
        - [Using a .env File](#using-a-env-file)
        - [Modifying Docker Compose Configuration](#modifying-docker-compose-configuration)
        - [Setting Variables via Command Line](#setting-variables-via-command-line)
    - [Running the Application](#running-the-application)
        - [Option 1: Running with Maven](#option-1-running-with-maven)
        - [Option 2: Running with Docker](#option-2-running-with-docker)
        - [Option 3: Running with Docker Compose](#option-3-running-with-docker-compose)
        - [Option 4: Running from the Executable JAR](#option-4-running-from-the-executable-jar)
- [Features](#features)
    - [Auth](#auth)
    - [Rooms](#rooms)
    - [Bookings](#bookings)
    - [Maintenance Windows](#maintenance-windows)
- [Other Features](#other-features)
    - [CI/CD](#cicd)
    - [Code Style Formatting](#code-style-formatting)
    - [Checkstyle Plugin](#checkstyle-plugin)
    - [Security](#security)
        - [CORS](#cors)
    - [Internationalization](#internationalization)
- [Testing](#testing)
    - [Running Tests](#running-tests)
    - [Testing with Real Database](#testing-with-real-database)
    - [Controller-Level Testing](#controller-level-testing)
    - [Efficiency and Coverage](#efficiency-and-coverage)
- [License](#license)
- [Contact Information](#contact-information)

## Requirements

* Java 22
* PostgreSQL Database
* Maven
* A modern IDE like IntelliJ IDEA, Eclipse, or VS Code (optional but recommended)

## Installation and Setup

This section outlines multiple ways to run the application. You can choose the method that best suits your development
environment and preferences.

### Common Installation

Before proceeding with any of the methods below, ensure you follow these steps:

#### 1. Clone the repository:

To get started, clone the repository to your local machine using the following command:

```bash
git clone https://github.com/yourusername/mashreq-coding-test.git
```

#### 2. Navigate to the project directory:

After cloning the repository, change your working directory to the project's root directory:

```bash
cd mashreq-coding-test
```

### Create Database

I chose PostgreSQL as the database for storing application data due to its robustness and wide adoption in the industry.
Before running the project, you need to ensure a PostgreSQL instance is available. There are multiple ways to set up a
PostgreSQL database so feel free to use whatever method you prefer. Below outlines 2 possible opitons:

#### Option 1: Using Docker Compose

To simplify the setup process, you can use the Docker Compose configuration provided in this repository to start a
PostgreSQL instance. This approach is especially useful if you want to avoid manual installation and configuration.

**1. Ensure Docker and Docker Compose are Installed:** Verify that Docker and Docker Compose are installed on your
system. You can check their versions with:

```bash
docker --version
docker compose --version
```

If either is missing, install Docker from the Docker website and Docker Compose from the Docker Compose installation
guide.

**2. Use Docker Compose to Start PostgreSQL:** Navigate to the directory containing
the `docker/docker-compose-infra.yml` file and start the PostgreSQL container with the following command:

```bash
docker compose -f docker-compose-infra.yml up -d
````

This command will start a PostgreSQL instance as defined in the docker-compose-infra.yml file.

3. **Verify the Database:** Ensure that the PostgreSQL container is running correctly. You can check the container
   status using:

```bash
docker ps
````

**4. Configure the Application:** The application’s Flyway migrations will automatically create the necessary tables and
schema
when the application is deployed.

By using Docker Compose, you can streamline the setup process and ensure a consistent environment for the database.

#### Option 2: Manual Setup

**1. Install PostgreSQL:** If not already installed, download and install PostgreSQL from the official website.
**2. Start the PostgreSQL Service:** Ensure the PostgreSQL service is running on your machine. On most systems, you can
start the service using:

```bash
sudo service postgresql start
```

**3. Create the Database:** Open the PostgreSQL command-line interface or use a GUI tool like pgAdmin to create a new
database. For example, using the command line:

```sql
CREATE DATABASE mashreq;
```

**4. Configure the Application:** The application’s Flyway migrations will automatically create the necessary tables and
schema when the application is deployed.

### Environment Configuration

Depending on your setup, you'll need to configure the application’s environment variables or properties. Here’s how you
can do this:

#### Using a .env File

The application uses environment variables defined in a .env.template file. Create a .env file in the root of your
project directory and set the variables as follows.
Example .env file:

```env
DATABASE_HOSTNAME=localhost
DATABASE_NAME=mashreq
DATABASE_USERNAME=yourusername
DATABASE_PASSWORD=yourpassword
OPENAPI_USERNAME=youropenapiusername
OPENAPI_PASSWORD=youropenapipassword
```

Ensure you customize these values according to your specific configuration.

### Modifying Docker Compose Configuration:

If you are using Docker Compose, you can set environment variables directly in the docker-compose.yml file. Update this
file with your database and API documentation credentials.

#### Setting Variables via Command Line:

You can also set environment variables directly in the command line when running the application. This method is useful
for temporary changes or testing.

#### For Maven:

```bash
DATABASE_HOSTNAME=localhost \
DATABASE_NAME=mashreq \
DATABASE_USERNAME=yourusername \
DATABASE_PASSWORD=yourpassword \
OPENAPI_USERNAME=youropenapiusername \
OPENAPI_PASSWORD=youropenapipassword \
mvn spring-boot:run
```

#### For Docker:

```bash
docker run -e DATABASE_HOSTNAME=localhost \
-e DATABASE_NAME=mashreq \
-e DATABASE_USERNAME=yourusername \
-e DATABASE_PASSWORD=yourpassword \
-e OPENAPI_USERNAME=youropenapiusername \
-e OPENAPI_PASSWORD=youropenapipassword \
-p 8080:8080 mashreq-coding-test
```

Replace the placeholder values with the actual credentials.

#### Environment Variables

* DATABASE_HOSTNAME - hostname for data store, e.g. localhost.
* DATABASE_NAME - the name of the database, e.g. mashreq.
* DATABASE_USERNAME - Username for data store.
* DATABASE_PASSWORD - Password for data store.
* OPENAPI_PASSWORD - Password for open api documentation
* OPENAPI_USERNAME - Username for open api documentation

### Running the Application

You have several options for running the application, depending on your environment and tools.

#### Option 1: Running with Maven

Maven is a build automation tool primarily used for Java projects. To run the application using Maven, follow these
steps:

**1. Ensure Maven is installed:**

Make sure Maven is installed on your system. You can verify this by running:

```bash
mvn -v
```

If Maven is not installed, follow the Maven installation guide to set it up.

**2. Build the project:**

Use Maven to compile and package the application. Run the following command:

```bash
mvn clean install
```

**3. Run the application:**

Start the application using the Spring Boot Maven plugin:

```bash
mvn spring-boot:run
```

The application will start, and you can access it at http://localhost:8080.

#### Option 2: Running with Docker

Docker provides a consistent environment to run applications, making it easier to manage dependencies and
configurations. For this option the code must have already been packaged into a `.jar`, this can be done
by running the following command.

```bash
mvn clean package
```

Alternatively you can pull the docker image from my dockerhub repository `ajhelsby/mashreq-coding-test`

**1. Ensure Docker is installed:**

Verify that Docker is installed on your system by running:

```bash
docker --version
```

If Docker is not installed, download and install it from the Docker website.

**2. Build the Docker image:**

Use the following command to build a Docker image of the application:

```bash
docker build -f docker/Dockerfile -t mashreq-coding-test .
````

Ensure you are in the base folder of the repository when running the above

**3. Run the Docker container:**

Start the application in a Docker container with the following command:

```bash
docker run -p 8080:8080 mashreq-coding-test
```
Ensure the env variables are set as suggested above.

The application will be available at http://localhost:8080.

#### Option 3: Running with Docker Compose

Using Docker Compose provides the easiest setup, as it includes both the PostgreSQL database and the API in a single
configuration. This option is recommended for a seamless and integrated environment.

**1. Ensure Docker and Docker Compose are installed:**

Verify that Docker and Docker Compose are installed on your system. You can check their versions with:

```bash
docker --version
docker-compose --version
```

If either is missing, install Docker from the Docker website and Docker Compose from the Docker Compose installation
guide.

**2. Start the application using Docker Compose:**

Navigate to the directory containing the `docker/docker-compose.yml` file and start the application with the following
command:

```bash
docker compose -f docker-compose.yml up
```

This command will start both the PostgreSQL database and the application in their respective containers. The application
will be available at http://localhost:8080.

Note: The Dockerized version of the application has been created and is stored on Docker Hub under the repository
ajhelsby/mashreq-coding-test.

#### Option 4: Running from the Executable JAR

If you prefer to run the application directly from a JAR file, follow these steps:

**1. Build the executable JAR:**

Create an executable JAR file using Maven:

```bash
mvn clean package
```

This command will generate a JAR file in the target directory.

**2. Run the JAR file:**

Use the following command to start the application:

```bash
java -jar target/mashreq-coding-test-1.0-SNAPSHOT.jar
```

Replace 1.0 with the actual version number if different.

The application will run at http://localhost:8080.

## Features

This application provides a robust set of features designed to facilitate effective conference room management and
booking. Below is a detailed overview of the key features included in the application. While the application is running,
you can explore the full API specification and interact with the API endpoints through the Swagger interface available
at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).

### Auth

While not a requirement for the test, I have implemented a simple database authentication mechanism using the built-in
JPA functionality and Spring Security. The authentication system is designed with extensibility in mind, as described
further down in the README.
The core components of the authentication system are:

- **Spring Security Integration:** Utilizes Spring Security to manage authentication and authorization.
- **JWT-Based Authentication:** Uses JSON Web Tokens (JWT) to secure API endpoints and manage user sessions.
- **Endpoints:**
    - **POST /api/v1/auth/signup:** Used for user registration.
    - **POST /api/v1/auth/login:** Used for user login and token generation.

Although additional features such as account verification and forgotten password endpoints are typically part of a
comprehensive authentication system, the focus for this implementation was on the core booking functionalities. These
additional features can be integrated as needed in future enhancements.

### Rooms

The Rooms module is a central component of the application, designed to manage and search conference rooms. It features
a single endpoint for querying room availability:

- **Endpoint:** `GET /api/v1/rooms`
- **Query Parameters:**
    - `startDate` - The start date for the room availability query.
    - `endDate` - The end date for the room availability query.
    - `numberOfPeople` - The number of people requiring the room.

#### Functionality

- **Room Search:** The endpoint allows users to search for available meeting rooms based on the provided query
  parameters. If no parameters are specified, the response includes details of all rooms. When parameters are provided,
  only rooms matching the criteria are returned.
- **Database Integration:** Rooms data is managed via a Flyway database migration, ensuring that the room details are
  consistently applied and versioned.
- **Response Details:** The response includes:
    - A list of room details.
    - Any bookings scheduled for the day.
    - Available time slots, showing the start and end times when the room is free.

#### Assumptions

- **Non-Paged Response:** The response is currently not paginated due to the limited number of rooms. However,
  pagination could be introduced if the number of rooms were to increase significantly in the future.

### Bookings

The Bookings module is a critical component of the application, providing endpoints to manage conference room bookings.
This section details the available endpoints and the functionality behind them:

#### Endpoints

- **POST /api/v1/bookings**: Creates a new booking.
- **DELETE /api/v1/bookings/:id**: Cancels an existing booking.

#### Booking Creation

The booking creation endpoint handles the core functionality of the application and includes a range of validations to
ensure correct and optimal booking:

- **Validation Rules:**
    - **Time Intervals:** Ensures that `startTime` and `endTime` are specified in 15-minute intervals and are set to the
      current date.
    - **Time Order:** Validates that `startTime` is before `endTime`.
    - **Capacity Check:** Ensures that `numberOfPeople` is greater than 1 and does not exceed the maximum capacity of
      the room allocated. Room capacity is dynamically verified by querying the room table, allowing for future
      scalability if room capacities are adjusted.

- **First Come, First Served:** To enforce a first-come, first-served policy, the endpoint utilizes transactional
  management and optimistic locking. This approach helps maintain consistency and avoid conflicts when multiple booking
  requests are processed simultaneously.
- **Optimum room allocation:** Creating a booking does not require a room to be passed, instead it will find the best
  suited room based on the time and number of people attending the meeting.

#### Booking Cancellation

- **Soft Delete:** The cancellation endpoint performs a soft delete by setting the booking status to "cancelled" rather
  than permanently deleting the record. This approach maintains an audit trail of all bookings and cancellations, which
  could be enhanced with a more detailed auditing mechanism in future versions.

#### Possible Improvements

- **Asynchronous Processing:** To handle high booking volumes more efficiently, consider implementing asynchronous
  processing for booking requests. This approach would allow operations to be performed in the background, potentially
  improving response times. Additionally, integrating a messaging system to notify users of successful bookings would
  enhance the user experience.

### Maintenance Windows

The Maintenance Windows feature manages scheduled maintenance periods for conference rooms. This feature is built with
flexibility and extensibility, allowing it to support various types of recurring bookings beyond maintenance.

#### Implementation

- **Data Model:** Maintenance windows are managed using the `RecurringBookings` object. This object is designed to
  handle different types of recurring bookings and extends the `AbstractBooking` class. The `BookingType` attribute
  within `RecurringBookings` helps define the nature of each recurring event, including maintenance windows.

- **Database Integration:** Maintenance windows are stored in the `RecurringBookings` table. Entries for maintenance
  windows have been added using Flyway migrations, which allows for structured and versioned database changes. This
  setup supports maintenance scheduling on a per-room basis, enabling different maintenance times for individual rooms.

#### Recurring Maintenance

- **Frequency Configuration:** Maintenance windows are currently set to recur daily, with the system assuming a fixed
  daily recurrence period. Future updates could introduce configurable recurrence patterns (e.g., weekly, monthly) to
  provide greater flexibility for various recurring bookings.

#### Booking Validation

- **Conflict Detection:** During the booking creation process, the system checks for any overlaps between the requested
  booking time and existing maintenance windows. If a conflict is detected, the system returns an appropriate error
  message, preventing bookings during maintenance periods.

#### Extensibility

The design of `RecurringBookings` ensures that the system is adaptable to different types of recurring bookings. Future
enhancements may include:

- **Flexible Recurrence Patterns:** Adding support for various recurrence intervals (e.g., weekly, monthly) to
  accommodate different booking needs.
- **Customizable Scheduling:** Providing more granular control over recurring events, such as specific times or
  frequencies based on the type of booking.

## Other Features

### CI/CD

Although CI/CD is not a requirement for this task, the repository includes a well-defined GitHub Actions pipeline to
automate various stages of the development workflow. The pipeline is designed to ensure code quality, automate testing,
and facilitate deployment. The CI/CD pipeline includes the following jobs:

- **`lint-pr`**: This job checks that pull request (PR) names adhere to a specified syntax. It helps maintain a
  consistent naming convention across the project.

- **`release-please`**: Utilizes the Release Please tool to manage and track project releases. This job automates
  versioning and changelog generation, ensuring that releases are documented and versioned appropriately.

- **`pr`**: Runs tests against a real PostgreSQL instance to validate the application’s functionality and ensures the
  build is successful. This job helps catch issues early by performing integration tests in a controlled environment.

- **`build-release`**: Dockerizes the application and pushes the resulting Docker image to Docker Hub. This job ensures
  that the application is containerized correctly and is readily available for deployment.

These CI/CD processes are configured in the `.github/workflows` directory. Each job is designed to streamline
development, improve code quality, and support reliable deployments.

For details on configuring or extending these pipelines, refer to the individual workflow YAML files in
the `.github/workflows` directory.

### Code Style formatting

I have implemented Google Java Style for code formatting in this project. Here’s how to set it up in IntelliJ IDEA:
**Install the Google Java Format Plugin:**
I added the Google Java Format plugin to IntelliJ IDEA by navigating to the IDE's settings and selecting the Plugins
category. In the Marketplace tab, I searched for the google-java-format plugin and clicked the Install button.

**Enable the Plugin:**
After installation, the plugin is disabled by default. To enable it for the current project, I went to IntelliJ IDEA →
Preferences... → Other Settings → google-java-format Settings (on macOS) and checked the Enable google-java-format
checkbox. A notification will also prompt this action when the project is first opened.

**Configure Code Reformatting:**
With the plugin enabled, it replaces the default Reformat Code action. I can trigger this by selecting Code → Reformat
Code from the menu or using the Ctrl-Alt-L (by default) keyboard shortcut.

**Handle Import Ordering:**
Unfortunately, the plugin does not manage import ordering. To address this, I imported the IntelliJ Java Google Style
file from docs/intellij-java-google-style.xml into IntelliJ IDEA. I did this by going to Preferences → Editor → Code
Style → Java and applying the provided style file to ensure consistent import ordering.

### Checkstyle plugin

The Maven Checkstyle plugin is configured to run during the build process. It is currently set to fail the build on
warning Checkstyle violations.

To add any suppression rules, update the checkstyle-suppressions.xml file as needed.

### Security

The application uses JSON Web Tokens (JWT) for securing user authentication.

> JSON Web Tokens are an open, industry standard RFC 7519 method for representing claims securely between two parties.
> For more information, visit [https://jwt.io/](https://jwt.io/).

**Current Authentication Implementation**

- **JPA (Default):** The default authentication method is handled via JPA.

The authentication system has been designed with flexibility and extensibility in mind. The architecture supports easy
integration of additional authentication methods. For example, integration with other authentication providers such as
AWS Cognito, Auth0, or any custom authentication service can be accomplished seamlessly due to the modular design of the
system.

To enable or configure different authentication methods, you can use the `modules.auth` property in
the `application.yml` file.

#### CORS

CORS is enabled at the server side by default. If the API will be accessed from multiple clients you
will need to add comma separated list of allowed origins to `application.yml` file:

`app.cors.allowed-origin: http://localhost:9000,http://www.example.com`

### Internationalization

The application supports internationalization. By default, all error messages returned by the API are localized in
English. To add support for additional languages, you need to copy the messages.properties file and add a language
suffix to the file name. For example, to add French language support, copy the messages.properties file to
messages_fr.properties and update the values accordingly.

## Testing

The development of this application was guided by Test-Driven Development (TDD) principles. TDD was chosen to ensure
that the application is robust, reliable, and meets the specified requirements from the outset. By writing tests before
implementing the actual functionality, it was possible to define clear requirements and ensure that each part of the
application works as intended.
The application includes both unit and integration tests to ensure correctness and reliability.

### Running Tests

To run all tests, use the following Maven command:

```bash
mvn test
```

### Testing with Real Database

Unit tests are configured to run against a real database. This approach is chosen because it provides a more accurate
representation of how the API interacts with the database in a real-world scenario. Testing against an actual database
helps uncover issues that may not be evident when using an in-memory database or mocks, such as:

- **Database-Specific Behavior:** Real databases can have behaviors or constraints that in-memory databases may not
  fully replicate. Using a real database ensures that database-specific SQL queries, constraints, and indexing are
  properly tested.

- **Integration Accuracy:** Tests against a real database help verify that the integration between the application's
  data access layer and the database works as expected. This includes testing complex queries, transactions, and data
  consistency.

- **Realistic Performance:** Running tests on a real database provides insights into the application's performance and
  how it handles real-world data and concurrency scenarios.

### Controller-Level Testing

In addition to unit tests, tests are also written at the controller level. This approach verifies the behavior of the
API endpoints, ensuring that they handle requests and responses correctly, and interact properly with the service layer.
Controller-level testing offers several benefits:

- **End-to-End Validation:** It allows for the validation of the entire request-response cycle, including how different
  components of the application work together.

- **Coverage of API Endpoints:** It ensures that all API endpoints are tested for various scenarios, including valid and
  invalid inputs, boundary conditions, and error handling.

- **Improved Reliability:** By testing at the controller level, you can ensure that the application behaves correctly
  from an end-user perspective, catching issues related to request processing, response formatting, and status codes.

### Efficiency and Coverage

While unit tests cover all lines of code and are essential for verifying individual components, integrating these tests
with a real database and at the controller level provides a more comprehensive validation. This approach ensures high
code coverage while also validating the application’s behavior in a realistic environment. It strikes a balance between
thorough testing and efficient test execution, leading to a more robust and reliable application.

## License

This project is licensed under the Apache-2.0 license. See the [LICENSE](LICENSE) file for details.

## Contact Information

For any questions or support, please contact [ajhelsby@gmail.com](mailto:ajhelsby@gmail.com).
