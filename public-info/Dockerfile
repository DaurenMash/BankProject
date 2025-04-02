FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/public-info-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8091

ENTRYPOINT ["java", "-jar", "app.jar"]