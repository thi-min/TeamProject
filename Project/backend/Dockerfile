# Stage 1: 빌드 환경
FROM maven:3.9.6-eclipse-temurin-17 AS build

# 작업 디렉터리를 /app으로 설정
WORKDIR /app

# 현재 디렉터리(Project/backend)의 모든 파일을 /app으로 복사
COPY . /app

# Maven 빌드 실행
RUN mvn clean package -Dmaven.test.skip=true -ntp

# Stage 2: 실행 환경
FROM openjdk:17-jdk-slim

# 작업 디렉터리를 /app으로 설정
WORKDIR /app

# 빌드 Stage에서 생성된 JAR 파일을 복사
COPY --from=build /app/target/teamproject-0.0.1-SNAPSHOT.jar /app/app.jar

# 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]
