FROM openjdk:11-jre-slim
EXPOSE 8080
ADD /build/libs/poc-0.0.1-SNAPSHOT.jar docker.jar
ENTRYPOINT ["java", "-jar", "docker.jar"]
