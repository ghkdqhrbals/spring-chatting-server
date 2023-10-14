
FROM openjdk:17-alpine

WORKDIR /null

COPY /build/libs/spring-chatting-front-server.jar /null/app.jar
ENTRYPOINT java -jar app.jar

LABEL version="unspecified"
