FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY target/*.jar flash_scale.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "flash_scale.jar"]