FROM openjdk:17-ea-17-jdk-buster

WORKDIR /app

COPY /target/tasktracker-0.0.1-SNAPSHOT.jar /app/tasktracker.jar

ENTRYPOINT ["java", "-jar", "tasktracker.jar"]