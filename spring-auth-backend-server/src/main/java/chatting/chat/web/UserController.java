package chatting.chat.web;

import chatting.chat.domain.data.*;
import chatting.chat.domain.user.service.UserService;
import chatting.chat.web.vo.RequestAddUserVO;
import chatting.chat.web.error.CustomException;
import chatting.chat.web.error.ErrorResponse;
import chatting.chat.web.kafka.KafkaTopicConst;
import chatting.chat.web.kafka.dto.RequestUserChange;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.ServletRequest;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;


@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController extends KafkaTopicConst {
    private final UserService userService;
    private final KafkaTemplate<String, Object> kafkaProducerTemplate;
    private final HikariDataSource hikariDataSource;

    @GetMapping("/health-check")
    public String hello(ServletRequest request){
        return "Access auth-controller port "+ String.valueOf(request.getRemotePort());
    }
    /**
     * -------------- READ METHODS --------------
     */
    // 유저 조회
    @GetMapping("/user/{userId}")
    public DeferredResult<ResponseEntity<?>> findUser(@PathVariable("userId") String userId){
        DeferredResult<ResponseEntity<?>> dr = new DeferredResult<>();
        dr.onTimeout(() ->
                dr.setErrorResult(
                        ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                                .body("Request timeout occurred.")));
        userService.findById(userId, dr);
        return dr;
    }

    @GetMapping(value = "/deferredResult")
    public DeferredResult<String> useDeferredResult() {
        DeferredResult<String> deferredResult = new DeferredResult<>();
        deferredResult.onCompletion(() -> log.info("onCompletion"));
        ForkJoinPool.commonPool().submit(() -> {
            deferredResult.setResult("Results Here");
            log.info("Results set");
        });
        return deferredResult;
    }

    @GetMapping("/login")
    public DeferredResult<ResponseEntity<?>> login(@RequestParam("userId") String userId,@RequestParam("userPw") String userPw){
        printHikariCPInfo();
        DeferredResult<ResponseEntity<?>> dr = new DeferredResult<>();
        return userService.login(userId, userPw, dr);
    }

    // 로그아웃
    @GetMapping("/logout")
    public DeferredResult<ResponseEntity<?>> logout(@RequestParam("userId") String userId, @RequestParam("userPw") String userPw){
        DeferredResult<ResponseEntity<?>> dr = new DeferredResult<>();
        return userService.logout(userId, userPw, dr);
    }

    /**
     * -------------- CREATE METHODS --------------
     */
    // 유저 저장
    @PostMapping("/user")
    public DeferredResult<ResponseEntity<?>> addUser(@RequestBody RequestAddUserVO req) throws InterruptedException {
        DeferredResult<ResponseEntity<?>> dr = new DeferredResult<>();
        userService.save(req, dr);
        sendToKafkaWithKey(TOPIC_USER_CHANGE, new RequestUserChange(req.getUserId(), req.getUserName(),"","INSERT"), req.getUserId());
        return dr;
    }

    /**
     * -------------- DELETE METHODS --------------
     */
    // 유저 삭제
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> removeUser(@PathVariable("userId") String userId){
//        User user = userService.findById(userId);
//        userService.removeUser(userId);
//
//        // 같은 파티션에 삽입하여 메세지 전송 순서 보장
//        sendToKafkaWithKey(TOPIC_USER_CHANGE, new RequestUserChange(user.getUserId(),user.getUserName(),"","DELETE"), user.getUserId());

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
//                log.error("메세지 전송 실패={}", ex.getMessage());
            }
            @Override
            public void onSuccess(SendResult<String, Object> result) {
//                log.info("메세지 전송 성공 topic={}, key={}, offset={}, partition={}",topic, key, result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
            }
        });

        return toCF(future);
    }

    <T> CompletableFuture<T> toCF(ListenableFuture<T> lf){
        CompletableFuture<T> cf = new CompletableFuture<T>();
        lf.addCallback(s-> cf.complete(s), e-> cf.completeExceptionally(e));
        return cf;
    }
    private String printHikariCPInfo() {
        return String.format("HikariCP[Total:%s, Active:%s, Idle:%s, Wait:%s]",
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getTotalConnections()),
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getActiveConnections()),
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getIdleConnections()),
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection())
        );
    }

}
