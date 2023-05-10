# Spring Java 채팅서버구현
현재 프로젝트는 MSA 이전중 임으로 불안정합니다. 따라서 채팅서버의 stable 한 버전인 4.2.1v 로 태그를 이동하고 Instruction 에 따라 실행해주세요. 

## Versions

| Version                                                                                 | Last Update | Skills                                                                                                                                                                                                                                                                                     |
|-----------------------------------------------------------------------------------------|-------------| ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **[v1](https://github.com/ghkdqhrbals/spring-chatting-server/tree/v1)**                 | 2022.12.14  | WebSocket, Kafka, Spring-Data-Jpa, Thymeleaf, Interceptor, etc.                                                                                                                                                                                                                            |
| **[v2](https://github.com/ghkdqhrbals/spring-chatting-server/tree/v2)**                 | 2023.01.03  | ElasticSearch, Logstash, Kibana, WebSocket, Kafka, Spring-Data-Jpa, Thymeleaf, Interceptor, etc.                                                                                                                                                                                           |
| **[v3](https://github.com/ghkdqhrbals/spring-chatting-server/tree/v3.1.1)**             | 2023.01.14  | Test, Kafka-connector, ElasticSearch, Logstash, Kibana, WebSocket, Kafka, Spring-Data-Jpa, Thymeleaf, Interceptor, etc.                                                                                                                                                                    |
| **[v4](https://github.com/ghkdqhrbals/spring-chatting-server/tree/v4.0.0)**             | 2023.01.24  | WebFlux, CSS, Test, Kafka-connector, ElasticSearch, Logstash, Kibana, WebSocket, Kafka, Spring-Data-Jpa, Thymeleaf, Interceptor, etc.                                                                                                                                                      |
| **[v5](https://github.com/ghkdqhrbals/spring-chatting-server/tree/v5.0.0)(Proceeding)** | 2023.04.27  | **JWT, Spring-Security(role-based access control), Exception Handling, rabbitMQ, Spring-Cloud(apigateway, configuration server, discovery server)** ,WebFlux, CSS, Test, Kafka-connector, ElasticSearch, Logstash, Kibana, WebSocket, Kafka, Spring-Data-Jpa, Thymeleaf, Interceptor, etc. |


# INDEX
1. [Current Architecture(v4)](#current-architecturev4)
2. [Running with Docker](#running-with-docker)
   * [Backend Server](#backend)
   * [ELK stack](#elk-stack)
3. [Update Logs](#update-logs)

To see the description of functions and simulation video, here is youtube link : [youtube link](https://www.youtube.com/watch?v=3VqwZ17XyEQ&t=237s)

## Proceeding... Architecture(v5)
![chatSeq](img/v5/21.svg)

## Architecture(v4)
![chatSeq](img/v3/v3.1.0.png)

## Running with Docker
### Backend
1. Run `./gradlew build` in each directory(spring-auth-backend-server, spring-chatting-backend-server)
2. In root directory, run `docker-compose -f docker-compose.yaml up -d`
3. Run `sh ./install-jdbc-connector.sh` for installing jdbc-sink connector and copy to kafka-connector container(container will be restarted)
4. Send HTTP request to kafka-connector as below for configuring schema in source/sink connector
    * Source Connector
    
    ```
    {
        "name": "source-connector",
        "config": {
            "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
            "plugin.name": "pgoutput",
            "database.hostname": "chatting-db-2",
            "database.port": "5434",
            "database.user": "postgres",
            "database.password": "password",
            "database.dbname" : "chat2",
            "database.server.name": "dbserver5434",
            
            "transforms": "unwrap,addTopicPrefix",
            "transforms.unwrap.type": "io.debezium.transforms.ExtractNewRecordState",
            "transforms.addTopicPrefix.type":"org.apache.kafka.connect.transforms.RegexRouter",
            "transforms.addTopicPrefix.regex":"(.*)",
            "transforms.addTopicPrefix.replacement":"$1"
        }
    }   
    ```

    * Sink Connector
    
    ```
    {
        "name": "sink-connector",
        "config": {
            "connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
            "task.max" : 5,
            "topics": "dbserver5434.public.user_table,dbserver5434.public.friend,dbserver5434.public.room,dbserver5434.public.participant,dbserver5434.public.chatting",
            
            "connection.url": "jdbc:postgresql://chatting-db-1:5433/chat1",
            "connection.user":"postgres",
            "connection.password":"password",
            "insert.mode": "upsert",
            "pk.mode": "record_key",
            "tombstones.on.delete": "true",
    
            "key.converter": "org.apache.kafka.connect.json.JsonConverter",
            "key.converter.schemas.enable": "true",
            "value.converter": "org.apache.kafka.connect.json.JsonConverter",
            "value.converter.schemas.enable": "true",
            "transforms": "unwrap,dropPrefix",
            "transforms.unwrap.type": "io.debezium.transforms.ExtractNewRecordState",
            "auto.create": "true",
            "auto.evolve":"true",
            "transforms.dropPrefix.type":"org.apache.kafka.connect.transforms.RegexRouter",
            "transforms.dropPrefix.regex":"dbserver5434(.*)$",
            "transforms.dropPrefix.replacement":"$1",
    
            "batch.size": "1"
        }
    }   
    ```

5. Send HTTP reqeust to chatServer
    > example
    > 
    > ```
    > POST http://localhost:8060/chat/user
    > {
    >    "userId":"Hwangbo",
    >    "userName":"황보규민"
    > }
    > ```

6. See Results in Kafdrop [http://localhost:9000/](http://localhost:9000/)

### ELK stack
1. Please cloning [docker-elk](https://github.com/deviantony/docker-elk) for running elk stacks
2. Edit `/docker-elk/logstash/pipeline/logstash.conf` with following configurations
    
   ```
    input {
        kafka {
            type => "analysis1"
            bootstrap_servers => "kafka1:9092,kafka2:9092,kafka3:9092"
            group_id => "logstash-user-add"
            topics => "log-user-add"
            codec => "json"
            consumer_threads => 2
        }
    
        kafka {
            type => "analysis2"
            bootstrap_servers => "kafka1:9092,kafka2:9092,kafka3:9092"
            group_id => "logstash-chat"
            topics => "log-user-chat"
            codec => "json"
            consumer_threads => 3
        }
    }
    
    output {
        if [type] == "analysis1" {
            elasticsearch {
                hosts => "elasticsearch:9200"
                index => "new-user"
                workers => 1
                user => "elastic"
                password => "${LOGSTASH_INTERNAL_PASSWORD}"
            }
        }
        if [type] == "analysis2" {
            elasticsearch {
                hosts => "elasticsearch:9200"
                index => "chat"
                workers => 1
                user => "elastic"
                password => "${LOGSTASH_INTERNAL_PASSWORD}"
            }
        }
    }
    ```
   
3. Run `docker-compose -f docker-elk/docker-compose-es.yml up -d` in root directory
4. In Kibana[http://localhost:5601](http://localhost:5601), create index of new-user and chat

## Update Logs
### Update[v5.0.0]
* MSA integration process


### Update[v4.2.1]

* Fix performance issue(Insert 10K chatting required time **80 sec** -> **29 sec**)
  * Update list
    * batch
      * before : 1
      * after : 100
    * chatting_id generation algorithm
      * before : from db sequence
      * after : random.UUID
    * add db parallel processor 
      * before : 1
      * after : 8
    * reduce query frequency per addChatRequest
      * before : 6
      * after : 4
    * add chatServer
      * before : 1
      * after : 2
> Before : ( get response ) **125. / sec**
>![img](img/v4/2.png)

> After : ( get response ) **344. / sec** 
>![img](img/v4/8.png)


### Update[v4.2.0]
* Add WebFlux
  * chatting
* Add Kibana Graph
  * new-user traffic
  * chat traffic

### Update[v4.1.0]
* Add WebFlux(To see more details about what is remaining, [visit blog](https://ghkdqhrbals.github.io/posts/chatting(16)))
    * Complete List
        * logout(non-blocking)
        * change user status
        * add/get user chat room list
* Add getUser api in ChatServer
* Edit userService.makeRoomWithFriends where setting room name

### Update[v4.0.1]
* Bug fix
  * 4xx response when send logout [Solved Issue #13](https://github.com/ghkdqhrbals/spring-chatting-server/issues/13)

### Update[v4.0.0]
* Add WebFlux for communicating backend by sending HTTP request through backend-api-gateway
  * Complete List
    * login
    * register
    * home
    * add friend
    
* Edit HTML and CSS files(But still, more process remain)
  * loginForm.html
  * addUserForm.html
  * addFriendForm.html
  * users.html

* Bug fix
  * infinite recursion in json response [Solved Issue #12](https://github.com/ghkdqhrbals/spring-chatting-server/issues/12)
    * by adding ResponseGetFriend class for intermediate class(dto)

* Change backend-gateway config
  * port `8080` -> `8060`
  * listen ip `localhost` -> `127.0.0.1`

### Update[v3.1.1]
* Add more mock test and error-code

### Update[v3.1.0]
* Bug fix
  * Nginx Proxy Issue [Solved Issue #5](https://github.com/ghkdqhrbals/spring-chatting-server/issues/5)
  * Null ID exception [Solved Issue #7](https://github.com/ghkdqhrbals/spring-chatting-server/issues/7)
* Add configurations for sink multiple topics with Kafka connector
  * Source Connector
  * Sink Connector

### Update[v3.0.1]
* Bug fix : add logstash configuration and compose file name

### Update[v3.0.0]
1. Add Kafdrop for simple visualization
2. Update **Uni-directional DB sync** with kafka connector
   * Configure Source Connector with Debezium
   * Configure Sink Connector with JDBC-Sink-Connector that load from Confluent

### Update[v2.0.0]
* Visualized Kafka Traffics and others
  * Kibana
  * UI for Kafka
* 모노서버 분리
   * 유저인증서버 + 채팅서버
* Kafka 멀티 브로커 설정
* ElasticSearch 연동
* Logstash input filter 설정
* Kibana 연동 및 시각화 설정
* Docker container화

### Update[v1.1.1]
* Kafka 추가
   * localhost:9092 Broker 설정
   * Producer/Consumer 설정
   * Spring - Kafka 연동
* Make Sequence Diagram

### Update[v1.1.0]
* 채팅방 STOMP-WebSocket 실시간 양방향 통신 추가 
 
### Update[v1.0.0]
* 기본적인 JPA 설정(Repository 생성)-PostgresDB 연동
   * 사용자, 채팅방, 채팅참여자, 친구를 저장할 수 있는 Repository 생성
* UserService 생성
   * UserRepository, FriendRepository, RoomRepository, ParticipantRepoisotry들을 Transactional 하게 관리하도록 설정

* UserController 생성
   * 여러가지 DTO 생성 + Thymeleaf 연동
* Login Filter 추가
   * 클라이언트 쿠키에 인증정보추가
* Form Error Handling
  * 공통처리
    * null값 입력
    * 입력값 가운데 space
  * **loginForm**
    * 일치하지 않는 ID/PW
  * **addFriendForm**
    * 존재하지 않는 유저를 친구추가
  * **addUserForm**
    * Email 형식 불일치
    * 존재하는 ID
