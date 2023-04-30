FROM openjdk:17-alpine


ENV APP_HOME=/customer

WORKDIR $APP_HOME

COPY /build/libs/customer-service-0.0.1-SNAPSHOT.jar $APP_HOME/app.jar
ENTRYPOINT java -jar app.jar