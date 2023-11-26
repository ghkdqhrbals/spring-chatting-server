
FROM openjdk:17-alpine

WORKDIR /null

COPY /build/libs/config-service.jar /null/app.jar
ENTRYPOINT java -jar app.jar

LABEL version=""
