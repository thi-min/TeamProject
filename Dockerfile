# 사용할 기본 이미지를 정의합니다.
FROM openjdk:17-jdk-slim

# 작업 디렉터리를 /app으로 설정합니다.
WORKDIR /app

# TeamProject/Project/backend 디렉터리의 모든 파일을 컨테이너의 /app 디렉터리로 복사합니다.
# 이렇게 하면 pom.xml 파일이 /app/pom.xml에 위치하게 됩니다.
COPY Project/backend/ /app

# 이제 /app 디렉터리로 이동하여 Maven 빌드를 실행합니다.
# pom.xml 파일이 /app에 있으므로 추가적인 WORKDIR 변경이 필요 없습니다.
RUN mvn package -Dmaven.test.skip=true -e -ntp

# JAR 파일이 생성된 target 디렉터리로 이동합니다.
WORKDIR /app/target

# 컨테이너가 시작될 때 실행될 명령어를 지정합니다.
# 생성된 JAR 파일의 이름을 정확하게 지정해야 합니다.
# (예: teamproject-0.0.1-SNAPSHOT.jar)
CMD ["java", "-jar", "teamproject-0.0.1-SNAPSHOT.jar"]
