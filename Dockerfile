FROM openjdk:17-jdk-slim
VOLUME /tmp
EXPOSE 8080
ADD target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]