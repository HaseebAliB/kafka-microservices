# Stage 1: Build
FROM maven:3.9.4-eclipse-temurin-17 AS build

WORKDIR /app

# Copy entire project (IMPORTANT)
COPY . .

RUN ls -R /app
# Build only ProductService module
RUN mvn clean package -DskipTests -pl ProductService -am

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/ProductService/target/ProductService-*.jar app.jar

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

CMD ["java", "-jar", "/app/app.jar"]