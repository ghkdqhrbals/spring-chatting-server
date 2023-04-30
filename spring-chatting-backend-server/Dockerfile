FROM openjdk:17-alpine


ENV APP_HOME=/chat

WORKDIR $APP_HOME

COPY /build/libs/spring-chatting-backend-server-0.0.1-SNAPSHOT.jar $APP_HOME/app.jar
ENTRYPOINT java -jar app.jar