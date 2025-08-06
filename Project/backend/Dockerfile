FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . /app
RUN mvn clean package -Dmaven.test.skip=true -ntp

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/teamproject-0.0.1-SNAPSHOT.jar /app/app.jar
CMD ["java", "-jar", "app.jar"]
