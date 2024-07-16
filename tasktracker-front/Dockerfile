FROM openjdk:17-ea-17-jdk-buster

WORKDIR /frontapp

COPY /target/tasktracker-frontend-1.0.0.jar /frontapp/tasktracker_front.jar

ENTRYPOINT ["java", "-jar", "tasktracker_front.jar"]