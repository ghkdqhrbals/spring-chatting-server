
FROM openjdk:17-alpine

WORKDIR /null

COPY /build/libs/discovery-1.0.2.jar /null/app.jar
ENTRYPOINT java -jar app.jar

LABEL version="1.0.2"
