package chatting.chat.web;

import chatting.chat.domain.data.*;
import chatting.chat.domain.user.service.UserService;
import chatting.chat.web.dto.RequestAddUserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;


@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * -------------- READ METHODS --------------
     */
    // 유저 조회
    @GetMapping("/{userId}")
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
        return ResponseEntity.ok("성공");
    }

    /**
     * -------------- CREATE METHODS --------------
     */
    // 유저 저장
    @PostMapping("")
    public ResponseEntity<?> addUser(HttpServletRequest r, @RequestBody RequestAddUserDTO request){

        log.info("request URI={}",r.getRequestURI());
        User user = new User(
                request.getUserId(),
                request.getUserPw(),
                request.getEmail(),
                request.getUserName(),
                LocalDate.now(),
                LocalDate.now(),
                LocalDate.now()
        );
        User save = userService.save(user);
        return ResponseEntity.ok(save);
    }

    /**
     * -------------- DELETE METHODS --------------
     */
    // 유저 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> removeUser(@PathVariable("userId") String userId){
        userService.removeUser(userId);
        return ResponseEntity.ok("성공");
    }

    /**
     * -------------- UPDATE METHODS --------------
     */
    // 유저 업데이트
    @PutMapping("/{userId}")
    public ResponseEntity<?> removeUser(@RequestBody User user){
        userService.updateUser(user);
        return ResponseEntity.ok("성공");
    }

}
