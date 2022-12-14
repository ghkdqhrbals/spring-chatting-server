# Spring Java 채팅서버구현
## Versions

| Version                                                                 | Last Update | Skills                                                          | 
|-------------------------------------------------------------------------|-------------|-----------------------------------------------------------------|
| **[v1](https://github.com/ghkdqhrbals/spring-chatting-server/tree/v1)** | 2022.12.14  | WebSocket, Kafka, Spring-Data-Jpa, Thymeleaf, Interceptor, etc. |

### Update[v1.1.1]
1. Kafka 추가
   * localhost:9092 Broker 설정
   * Producer/Consumer 설정
   * Spring - Kafka 연동
* Sequence Diagram
![chatSeq](img/chatting.png)
> 기존에는 클라이언트로부터 메세지를 전달받으면 MessageHandler에서 별 다른 과정 없이 바로 토픽으로 전송하였다.
> 
> 이제는 MessageHandler에서 Kafka에 전달하여 중앙서버를 거침으로써 다른 서버에서도 메세지를 가지고 다른 로직들을 수행할 수 있도록 설정.( ex) **로깅** + **어뷰저 관측** )

### Update[v1.1.0]
1. 채팅방 STOMP-WebSocket 실시간 양방향 통신 추가
> 1. `유저A`는 채팅방 입장 시, 서버의 /stomp/chat 엔드포인트와 연결
> 2. 채팅 전송 시, /pub/chat/message 로 ChatMessageDTO와 함께 전송
> 3. 서버는 MessageHandler을 통해 SimpleBroker의 TOPIC : /sub/chat/message로 전달
> 4. SimpleBroker은 현재 연결된 유저 중, 해당 토픽을 구독하고 있는 `유저B`, `유저C` 에게 전달
> 5. `유저B`, `유저C`는 Jquery로 ChatMessageDTO의 메세지 파싱 후 읽음

> 메세지를 실시간으로 분석하는 ML서버를 다른곳에서 진행하고 싶은데...
 
### Update[v1.0.0]
1. 기본적인 JPA 설정(Repository 생성)-PostgresDB 연동
   * 사용자, 채팅방, 채팅참여자, 친구를 저장할 수 있는 Repository 생성
2. UserService 생성
   * UserRepository, FriendRepository, RoomRepository, ParticipantRepoisotry들을 Transactional 하게 관리하도록 설정
> 이 때, `하나의 Service에서 다수의 Repository를 사용하는것이 여러 방면에서 옳은가?` 라는 고민이였다. 내린 결론은 `상관이 없다` 였으며, 이렇게 생각한 이유는 다음과 같다. DB관리에서 무엇보다도 중요한 것이 트랜젝션 관리이며, 트랜잭션의 정합성만 만족한다면 된다. 
> 
> 이것은 다수의 Repository를 사용해도 만족가능하다. Service의 function내에 다수의 Repository를 사용해도 function의 앞과 뒤에 BEGIN/COMMIT을, 그리고 에러시 ROLLBACK을 스프링에서 붙여주기때문에, 우리는 Service내에서의 트랜잭션의 정합성을 의심할 필요가 없다는 것이다. 따라서 다수의 Repository을 사용하는것에 대해 상관없다라는 결론을 내렸다.
3. UserController 생성
   * 여러가지 DTO 생성 + Thymeleaf 연동
4. Login Filter 추가
   * 클라이언트 쿠키에 인증정보추가
> 1. 유저로그인 시, 서버는 유저정보를 해싱하여 1) 세션에 저장, 2) 클라이언트의 쿠키에 삽입 한다.
> 2. 클라이언트는 이후 있을 통신에 쿠키값을 전송한다.
> 3. 서버의 필터는 전달받은 쿠키값과 서버의 세션에 저장된 값이 같을 때 인증성공으로 판단한다.
5. Form Error Handling
  * 공통
    * null값 입력
    * 입력값 가운데 space
  * **loginForm**
    * 일치하지 않는 ID/PW
  * **addFriendForm**
    * 존재하지 않는 유저를 친구추가
  * **addUserForm**
    * Email 형식 불일치
    * 존재하는 ID