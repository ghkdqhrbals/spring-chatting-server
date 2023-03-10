package chatting.chat.web;

import chatting.chat.domain.data.*;
import chatting.chat.domain.user.service.UserService;
import chatting.chat.web.dto.RequestAddUserDTO;
import chatting.chat.web.error.CustomException;
import chatting.chat.web.error.ErrorResponse;
import chatting.chat.web.kafka.KafkaTopicConst;
import chatting.chat.web.kafka.dto.RequestUserChange;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import static chatting.chat.web.util.LogTrace.logWithThread;


@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController extends KafkaTopicConst {
    private final UserService userService;
    private final KafkaTemplate<String, Object> kafkaProducerTemplate;



    /**
     * -------------- READ METHODS --------------
     */
    // 유저 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> findUser(@PathVariable("userId") String userId){
        User user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }

    // 로그인
    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestParam("userId") String userId,@RequestParam("userPw") String userPw){
        User user = userService.login(userId,userPw);
        return ResponseEntity.ok(user);
    }

    // 로그아웃
    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam("userId") String userId){
        userService.logout(userId);
        return ResponseEntity.ok("success");
    }

    /**
     * -------------- CREATE METHODS --------------
     */
    // 유저 저장
    @PostMapping("/user")
    public DeferredResult<ResponseEntity<?>> addUser(@RequestBody RequestAddUserDTO request){

        DeferredResult<ResponseEntity<?>> dr = new DeferredResult<>();

        User user = new User(
                request.getUserId(),
                request.getUserPw(),
                request.getEmail(),
                request.getUserName(),
                LocalDate.now(),
                LocalDate.now(),
                LocalDate.now()
        );

        // 유저 서비스를 통해 유저 저장
        saveUserHandler(dr, user);
        return dr;
    }

    private void saveUserHandler(DeferredResult<ResponseEntity<?>> dr, User user) {

        CompletableFuture
                .runAsync(()->{
                }).thenCompose(s->{
                    return userService.save(user);
                }).thenAccept( s1->{
                    sendToKafkaWithKey(TOPIC_USER_CHANGE, new RequestUserChange(user.getUserId(), user.getUserName(),"","INSERT"), user.getUserId());
                    dr.setResult(ResponseEntity.ok("success"));
                }).exceptionally(e->{
                    if (e.getCause() instanceof CustomException){
                        dr.setResult(ErrorResponse.toResponseEntity(((CustomException) e.getCause()).getErrorCode()));
                    }else{
                        dr.setResult(ResponseEntity.badRequest().body("default bad request response"));
                    }
                    return null;
                });
    }

    /**
     * -------------- DELETE METHODS --------------
     */
    // 유저 삭제
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> removeUser(@PathVariable("userId") String userId){
        User user = userService.findById(userId);
        userService.removeUser(userId);

        // 같은 파티션에 삽입하여 메세지 전송 순서 보장
        sendToKafkaWithKey(TOPIC_USER_CHANGE, new RequestUserChange(user.getUserId(),user.getUserName(),"","DELETE"), user.getUserId());

        return ResponseEntity.ok("success");
    }

    /**
     * -------------- UPDATE METHODS --------------
     */
    // 유저 업데이트
    @PutMapping("/user")
    public ResponseEntity<?> removeUser(@RequestBody User user){
        userService.updateUser(user);
        return ResponseEntity.ok("success");
    }

    /**
     * -------------- Utils --------------
     */



    private CompletableFuture<?> sendToKafkaWithKey(String topic,Object req, String key) {

        ListenableFuture<SendResult<String, Object>> future = kafkaProducerTemplate.send(topic,key, req);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("메세지 전송 실패={}", ex.getMessage());
            }
            @Override
            public void onSuccess(SendResult<String, Object> result) {
                log.info("메세지 전송 성공 topic={}, key={}, offset={}, partition={}",topic, key, result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
            }
        });

        return toCF(future);
    }

    <T> CompletableFuture<T> toCF(ListenableFuture<T> lf){
        CompletableFuture<T> cf = new CompletableFuture<T>();
        lf.addCallback(s-> cf.complete(s), e-> cf.completeExceptionally(e));
        return cf;
    }

}
