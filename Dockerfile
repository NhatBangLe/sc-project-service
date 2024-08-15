FROM maven:3.9.8-eclipse-temurin-21-alpine AS build

COPY src /app/src
COPY pom.xml /app

WORKDIR /app
RUN mvn clean install -Dmaven.test.skip=true

FROM eclipse-temurin:21-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/project-service.jar /app/app.jar

WORKDIR /app

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]