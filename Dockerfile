# Building the Spring Boot JAR
FROM gradle:8.4-jdk17 AS build
WORKDIR /app
COPY src ./src
COPY build.gradle ./build.gradle
COPY settings.gradle ./settings.gradle
RUN gradle clean build -x test

# Creating the Docker image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/kafka-producer-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9020
CMD ["java", "-jar", "app.jar"]