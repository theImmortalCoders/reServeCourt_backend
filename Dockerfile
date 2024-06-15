FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn package

FROM openjdk:17-jdk-alpine
ENV SPRING_PROFILES_ACTIVE prod
COPY --from=build /app/target/reserve_court_backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
