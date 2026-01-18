FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY target/ProductService-*.jar app.jar

CMD ["java", "-jar", "/app/app.jar"]