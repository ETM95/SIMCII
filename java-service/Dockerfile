FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -f pom.xml -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/target/java-service-1.0.0.jar ./java-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "java-service.jar"]
