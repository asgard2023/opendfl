FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY *.war app.jar
COPY *.yml .
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]