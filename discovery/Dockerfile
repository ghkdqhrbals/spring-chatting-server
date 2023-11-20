
FROM openjdk:17-alpine

WORKDIR /null

COPY /build/libs/discovery.jar /null/app.jar
ENTRYPOINT java -jar app.jar

LABEL version="unspecified"
