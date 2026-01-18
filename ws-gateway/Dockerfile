FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY target/ws-gateway-*.jar app.jar

CMD ["java", "-jar", "/app/app.jar"]