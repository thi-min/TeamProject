# 1. 사용할 기본 이미지를 정의합니다.
FROM openjdk:17-jdk-slim

# 2. 작업 디렉터리를 /app으로 설정합니다.
WORKDIR /app

# 3. 모든 프로젝트 소스 코드를 컨테이너의 /app 디렉터리로 복사합니다.
COPY . /app

# 4. project/backend 디렉터리로 이동하여 Maven 빌드를 실행합니다.
#    이전에 발생했던 pom.xml 오류를 해결하기 위함입니다.
WORKDIR /app/project/backend
RUN mvn package -Dmaven.test.skip=true -e -ntp

# 5. JAR 파일이 생성된 target 디렉터리로 이동합니다.
WORKDIR /app/project/backend/target

# 6. 컨테이너가 시작될 때 실행될 명령어를 지정합니다.
#    생성된 JAR 파일 이름을 정확하게 지정해야 합니다. (예: teamproject-0.0.1-SNAPSHOT.jar)
CMD ["java", "-jar", "teamproject-0.0.1-SNAPSHOT.jar"]
