# 빌드 이미지 (Maven 포함)
FROM maven:3.9.6-eclipse-temurin-17 AS build

# 작업 디렉토리
WORKDIR /app

# ✅ Project/backend 안의 실제 Maven 프로젝트 복사
COPY Project/backend/ /app

# 빌드 실행
RUN mvn clean package -Dmaven.test.skip=true -ntp


# 실행 이미지
FROM openjdk:17-jdk-slim

WORKDIR /app

# 위에서 만든 JAR 복사
COPY --from=build /app/target/teamproject-0.0.1-SNAPSHOT.jar /app/app.jar

# 실행
CMD ["java", "-jar", "app.jar"]
