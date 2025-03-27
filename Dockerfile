# Stage 1: Build the application
FROM jelastic/maven:3.9.5-openjdk-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src/ ./src

# Build the project using Maven
RUN --mount=type=cache,target=/root/.m2,rw mvn clean package -DskipTests

# Stage 2: Create a smaller image with the built JAR
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/FinancialSystem-0.0.1-SNAPSHOT.jar /opt/FinancialSystem/FinancialSystem-0.0.1-SNAPSHOT.jar

# Command to run the app with remote debugging enabled
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000", "-jar", "/opt/FinancialSystem/FinancialSystem-0.0.1-SNAPSHOT.jar"]
