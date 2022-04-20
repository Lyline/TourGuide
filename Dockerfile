FROM openjdk:8-jdk-alpine3.9
ARG JAR_FILE=build/libs/tourGuide-1.0.0.jar
COPY ${JAR_FILE} tourGuide-1.0.0.jar
ENTRYPOINT [ "java","-Dspring.profiles.active=prod", "-jar", "tourGuide-1.0.0.jar" ]