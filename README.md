# Mashreq Bank Coding Test

## Conference Room Booking REST API

This is a Java Spring Boot application, written as part of a coding test for Mashreq Bank. The project is a REST API
built to allow users to book meeting rooms within a company. It provides functionality to book one of four available
conference rooms based on the number of attendees and the specified time. The API ensures optimal room allocation and
adheres to the company's maintenance schedule.

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
Before running the project, you need to ensure a PostgreSQL instance is available. You have two options for setting up
the database:

#### Option 1: Using Docker Compose

To simplify the setup process, you can use the Docker Compose configuration provided in this repository to start a
PostgreSQL instance. This approach is especially useful if you want to avoid manual installation and configuration.

**1. Ensure Docker and Docker Compose are Installed:** Verify that Docker and Docker Compose are installed on your
system. You
can check their versions with:

```bash
docker --version
docker compose --version
```

If either is missing, install Docker from the Docker website and Docker Compose from the Docker Compose installation
guide.

**2. Use Docker Compose to Start PostgreSQL:** Navigate to the directory containing
the `docker/docker-compose-infra.yml` file
and start the PostgreSQL container with the following command:

```bash
docker compose -f docker-compose-infra.yml up -d
````

This command will start a PostgreSQL instance as defined in the docker-compose-infra.yml file.

3. **Verify the Database:** Ensure that the PostgreSQL container is running correctly. You can check the container
   status
   using:

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
configurations.

**1. Ensure Docker is installed:**

Verify that Docker is installed on your system by running:

```bash
docker --version
```

If Docker is not installed, download and install it from the Docker website.

**2. Build the Docker image:**

Use the following command to build a Docker image of the application:

```bash
docker build -t mashreq-coding-test .
````

**3. Run the Docker container:**

Start the application in a Docker container with the following command:

```bash
docker run -p 8080:8080 mashreq-coding-test
```

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
docker-compose up -d
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

Replace the placeholder values with your actual credentials.

#### Environment Variables

* DATABASE_HOSTNAME - hostname for data store, e.g. localhost.
* DATABASE_NAME - the name of the database, e.g. mashreq.
* DATABASE_USERNAME - Username for data store.
* DATABASE_PASSWORD - Password for data store.
* OPENAPI_PASSWORD - Password for open api documentation
* OPENAPI_USERNAME - Username for open api documentation

## Features

## Other Features

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

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contact Information

For any questions or support, please contact [ajhelsby@hotmail.com](mailto:ajhelsby@hotmail.com).
