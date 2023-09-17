
FROM openjdk:17-alpine

WORKDIR /null

COPY /build/libs/spring-chatting-backend-server-1.0.1.jar /null/app.jar
ENTRYPOINT java -jar app.jar

LABEL version="1.0.1"
