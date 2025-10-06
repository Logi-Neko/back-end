# Dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app

# copy bất kỳ jar nào trong target thành app.jar
COPY target/LogiNeko-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]