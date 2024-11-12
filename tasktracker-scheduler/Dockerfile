FROM eclipse-temurin:17-jdk-alpine AS build
ENV HOME=/usr/app
RUN mkdir -p "$HOME"
WORKDIR $HOME
COPY . $HOME
RUN --mount=type=cache,target=/root/.m2 ./mvnw -f "$HOME"/pom.xml clean package

FROM eclipse-temurin:17-jre-alpine
ARG JAR_FILE=/usr/app/target/*.jar
COPY --from=build $JAR_FILE /app/tasktracker_scheduler.jar
ENTRYPOINT ["java","-jar","/app/tasktracker_scheduler.jar"]