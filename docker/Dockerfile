FROM amazoncorretto:22-alpine

# Create an app folder
RUN mkdir /app

# Set /app as the working directory
WORKDIR /app

# Copy the built artifacts from the build stage
COPY target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]