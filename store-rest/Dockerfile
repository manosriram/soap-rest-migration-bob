FROM registry.access.redhat.com/ubi9/openjdk-17:latest AS build

USER root
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn package -DskipTests -q

FROM registry.access.redhat.com/ubi9/openjdk-17-runtime:latest

WORKDIR /app

COPY --from=build /app/target/store-rest-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
