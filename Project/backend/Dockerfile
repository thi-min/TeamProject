# jar 파일 빌드 (빌드 스테이지)
# Maven과 OpenJDK 11이 포함된 이미지를 사용합니다.
FROM eclipse-temurin:11 AS builder

# 컨테이너 내 작업 디렉토리를 /opt/app으로 설정합니다.
WORKDIR /opt/app

# 현재 빌드 컨텍스트(Project/backend)에서 .mvn 디렉토리, mvnw 스크립트, pom.xml 파일을 /opt/app으로 복사합니다.
# Dockerfile이 Project/backend/에 있으므로, 이 파일들은 현재 디렉토리(.)에 있습니다.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Maven 의존성을 미리 다운로드하여 빌드 속도를 높입니다.
RUN ./mvnw dependency:go-offline

# 현재 빌드 컨텍스트(Project/backend)에서 src 디렉토리를 /opt/app으로 복사합니다.
COPY ./src ./src

# Maven 빌드를 실행하여 JAR 파일을 생성합니다.
# 테스트를 스킵하고, install 단계를 거쳐 JAR 파일을 target 디렉토리에 만듭니다.
RUN ./mvnw clean install -Dmaven.test.skip=true

# jar 실행 (런타임 스테이지)
# 경량화된 OpenJDK 11 이미지를 사용해 최종 실행 환경을 만듭니다.
FROM eclipse-temurin:11 AS runtime

# 보안 강화를 위해 'worker' 그룹과 사용자를 생성합니다.
RUN addgroup --system --gid 1000 worker
RUN adduser --system --uid 1000 --ingroup worker --disabled-password worker
# 이후 명령은 'worker' 사용자로 실행됩니다.
USER worker:worker

# 컨테이너 내 작업 디렉토리를 /opt/app으로 설정합니다.
WORKDIR /opt/app

# 애플리케이션이 사용할 포트(8080)를 노출합니다. (정보성)
EXPOSE 8080

# 빌드 스테이지에서 생성된 JAR 파일을 /opt/app/app.jar로 복사합니다.
# pom.xml의 artifactId가 teamproject이므로, JAR 파일 이름은 teamproject-0.0.1-SNAPSHOT.jar입니다.
COPY --from=builder /opt/app/target/teamproject-0.0.1-SNAPSHOT.jar /opt/app/app.jar

# 컨테이너가 시작될 때 실행될 명령어를 정의합니다.
# Spring 프로필을 환경 변수 PROFILE로 설정하고, app.jar 파일을 실행합니다.
ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}", "-jar", "/opt/app/app.jar"]
