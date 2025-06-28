FROM openjdk:17-jdk-slim

WORKDIR /app

# Копируем JAR-файл внутрь контейнера
COPY target/money-transfer-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
