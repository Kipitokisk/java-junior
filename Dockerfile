FROM openjdk:17-jdk-slim AS builder
WORKDIR /app

RUN apt-get update && apt-get install -y maven

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=builder /app/target/*.jar /app/java-junior-test.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/java-junior-test.jar"]