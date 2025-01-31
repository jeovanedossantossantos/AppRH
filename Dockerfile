FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY src /app/src
COPY pom.xml /app


EXPOSE 8080
CMD ["mvn", "spring-boot:run"]
