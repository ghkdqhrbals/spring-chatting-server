
FROM openjdk:17-alpine

WORKDIR /null

COPY /build/libs/shop-user-service.jar /null/app.jar
ENTRYPOINT java -jar app.jar

LABEL version="unspecified"
