FROM eclipse-temurin:21-jdk-jammy AS build
COPY . /app
WORKDIR /app
RUN ./mvnw clean install -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-jammy
RUN addgroup --system spring && adduser --system spring && adduser spring spring
USER spring:spring
COPY --from=build /app/target/project-service.jar /app/app.jar
WORKDIR /app
CMD ["java", "-jar", "app.jar"]