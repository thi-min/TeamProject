### 1단계: Maven 빌드용 이미지
FROM maven:3.9.6-eclipse-temurin-17 AS build

# 작업 디렉토리 생성
WORKDIR /app

# 프로젝트 복사
COPY Project/backend/ /app

# 의존성 캐시 (선택 사항)
RUN mvn dependency:go-offline

# 실제 빌드 수행 (테스트 제외)
RUN mvn clean package -Dmaven.test.skip=true -e -ntp


### 2단계: 실행용 이미지 (가볍게)
FROM openjdk:17-jdk-slim

WORKDIR /app

# 위에서 빌드된 JAR 복사
COPY --from=build /app/target/teamproject-0.0.1-SNAPSHOT.jar /app/app.jar

# 실행 명령어 지정
CMD ["java", "-jar", "app.jar"]
