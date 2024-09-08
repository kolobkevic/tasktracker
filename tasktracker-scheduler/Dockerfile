FROM openjdk:17-ea-17-jdk-buster

WORKDIR /app

COPY ./target/tasktracker-scheduler-0.0.1-SNAPSHOT.jar /app/tasktracker_scheduler.jar

ENTRYPOINT ["java", "-jar", "tasktracker_scheduler.jar"]