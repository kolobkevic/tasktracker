FROM openjdk:17-ea-17-jdk-buster

WORKDIR /app

COPY ./target/tasktracker-email-sender-0.0.1-SNAPSHOT.jar /app/tasktracker_emailsender.jar

ENTRYPOINT ["java", "-jar", "tasktracker_emailsender.jar"]