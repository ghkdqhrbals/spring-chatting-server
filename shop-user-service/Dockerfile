
FROM amazoncorretto:17-alpine3.16-jdk

WORKDIR /null

COPY /build/libs/shop-user-service.jar /null/app.jar
ENTRYPOINT java -jar app.jar

LABEL version=""
