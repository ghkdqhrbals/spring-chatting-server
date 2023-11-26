
FROM openjdk:17-alpine

WORKDIR /app

COPY /build/libs/spring-chatting-front-server.jar /app/app.jar
# This part should be stored in the secret storage
COPY keystore.p12 /app/keystore.p12
ENTRYPOINT java -jar app.jar

LABEL version=""
