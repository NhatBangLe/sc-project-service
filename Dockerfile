FROM eclipse-temurin:21-jdk-jammy AS build
COPY . /app
WORKDIR /app
RUN ./mvnw clean install -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-jammy
RUN addgroup --system spring && adduser --system spring && adduser spring spring

COPY --from=build /app/target/project-service.jar /sc-project/app.jar
WORKDIR /sc-project
RUN mkdir logs
RUN chown spring:spring logs

USER spring:spring
CMD ["java", "-jar", "app.jar"]