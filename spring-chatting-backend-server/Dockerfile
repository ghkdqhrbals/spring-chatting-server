
FROM openjdk:17-alpine

WORKDIR /null

COPY /build/libs/spring-chatting-backend-server.jar /null/app.jar
ENTRYPOINT java -jar app.jar

LABEL version="unspecified"
