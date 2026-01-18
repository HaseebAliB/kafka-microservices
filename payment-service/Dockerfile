FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app
COPY target/payment-service-*.jar app.jar

CMD ["java", "-jar", "/app/app.jar"]