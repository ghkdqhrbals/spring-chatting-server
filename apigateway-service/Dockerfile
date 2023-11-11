
FROM openjdk:17-alpine

WORKDIR /null

COPY /build/libs/apigateway-service-0.0.1.jar /null/app.jar
ENTRYPOINT java -jar app.jar

LABEL version="0.0.1"
