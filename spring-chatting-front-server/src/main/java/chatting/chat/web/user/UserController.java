package chatting.chat.web.user;


import chatting.chat.domain.data.User;
import chatting.chat.web.dto.*;
import chatting.chat.web.error.CustomThrowableException;
import chatting.chat.web.error.ErrorResponse;
import chatting.chat.web.filters.cons.SessionConst;
import chatting.chat.web.friend.service.FriendService;
import chatting.chat.web.global.CommonModel;
import chatting.chat.web.kafka.dto.CreateChatRoomUnitDTO;
import chatting.chat.web.kafka.dto.RequestChangeUserStatusDTO;
import chatting.chat.web.login.util.CookieUtil;
import chatting.chat.web.user.dto.RequestUser;
import com.example.commondto.dto.friend.FriendResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    private WebClient webClient;

    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private FriendService friendService;

    private final SimpMessagingTemplate template;
    @Value("${backend.api.gateway}")
    private String backEntry;

    public UserController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @PostConstruct
    public void initWebClient() {
        log.info(backEntry);
        this.webClient = WebClient.create(backEntry);
    }

    // 유저 추가
    @GetMapping("/register")
    public String addUserPage(@ModelAttribute("userForm") UserForm form, Model model) {
        CommonModel.addCommonModel(model);
        return "users/addUserFormSingle";
    }

    @PostMapping("/register")
    public CompletableFuture<String> addUser(@Valid @ModelAttribute("userForm") UserForm form,
        BindingResult bindingResult,
        Model model) {

        // Form 에러 모델 전달
        if (bindingResult.hasErrors()) {
            return CompletableFuture.completedFuture("users/addUserFormSingle");
        }

        RequestUser req = new RequestUser();
        req.setUserId(form.getUserId());
        req.setUserPw(form.getUserPw());
        req.setEmail(form.getEmail());
        req.setUserName(form.getUserName());
        req.setRole("ROLE_USER"); // 기본적으로 일반 롤 부여

        log.trace("Send Request in main thread{}", req.toString());

        // 동시성을 위한 별도 스레드 풀 사용
        return CompletableFuture.supplyAsync(() -> {
            log.trace("Send Request inside another thread {}", req.toString());
            Flux<ServerSentEvent> temp = webClientBuilder.build().post()
                .uri("/user")
                .bodyValue(req)
                .retrieve()
//                    .onStatus(
//                            HttpStatus::is4xxClientError,
//                            r -> r.bodyToMono(ErrorResponse.class).map(CustomThrowableException::new))
//                    .onStatus(
//                            HttpStatus::is5xxServerError,
//                            r -> r.bodyToMono(ErrorResponse.class).map(CustomThrowableException::new))
                .bodyToFlux(ServerSentEvent.class).log();

            model.addAttribute("sseStream", temp);

            temp.doFinally((s) -> {
                log.info("Complete");
            }).subscribe((s) -> {
                log.info("{}", s);
            });
            return "redirect:/login";
//            return "redirect:/";
        }).exceptionally((e) -> {
            log.info("Exceptions!={}", e.getMessage());
            bindingResult.rejectValue("userId", null, e.getMessage());
            return "users/addUserFormSingle";
        });
    }


    // 유저 상태메세지 변경
    @GetMapping("/status")
    public String updateUserStatus(
        @ModelAttribute("userStatusUpdateDTO") UserStatusUpdateDTO form, Model model) {
        CommonModel.addCommonModel(model);
        return "users/updateStatusForm";
    }

    @PostMapping("/status")
    public String updateUserStatusForm(HttpSession session,
        @Valid @ModelAttribute("userStatusUpdateDTO") UserStatusUpdateDTO form,
        BindingResult bindingResult) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_MEMBER);

        // 상태 메세지 변경
        try {
            webClientBuilder.build().post()
                .uri("/chat/status")
                .bodyValue(
                    new RequestChangeUserStatusDTO(user.getUserId(), form.getStatusMessage()))
                .retrieve()
                .onStatus(
                    HttpStatus::is4xxClientError,
                    r -> r.bodyToMono(ErrorResponse.class).map(CustomThrowableException::new))
                .bodyToMono(String.class)
                .block();

        } catch (CustomThrowableException e) {
            log.info(e.getErrorResponse().getCode());
            log.info(e.getErrorResponse().getMessage());
            return "users/updateStatusForm";
        }

        return "redirect:/";
    }

    //채팅방 목록 조회
    @GetMapping(value = "/rooms")
    public String rooms(
        @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = true) User user,
        HttpSession session, Model model) {
        CommonModel.addCommonModel(model);
        try {
            Flux<ChatRoomDTO> response = webClientBuilder.build().get()
                .uri("/chat/rooms?userId=" + user.getUserId())
                .retrieve()
                .onStatus(
                    HttpStatus::is4xxClientError,
                    r -> r.bodyToMono(ErrorResponse.class).map(CustomThrowableException::new))
                .bodyToFlux(ChatRoomDTO.class);
            List<ChatRoomDTO> readers = response.collect(Collectors.toList())
                .share().block();

            model.addAttribute("list", readers);

        } catch (CustomThrowableException e) {

            log.info(e.getErrorResponse().getCode());
            log.info(e.getErrorResponse().getMessage());
            /**
             * TODO global error
             */
            return "users/rooms";
        }
        return "users/rooms";
    }

    //채팅방 개설


    // 패킷의 크기 또한 신경써야될듯
    // 채팅방
    @GetMapping("/chat")
    public String chattingRoom(
        @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = true) User user,
        @RequestParam Long roomId, @RequestParam String roomName, Model model) {
        CommonModel.addCommonModel(model);

        try {

            Flux<ChatRecord> response = webClient.mutate()
                .build()
                .get()
                .uri("/chat/chats?roomId=" + roomId)
                .retrieve()
                .onStatus(
                    HttpStatus::is4xxClientError,
                    r -> r.bodyToMono(ErrorResponse.class).map(CustomThrowableException::new))
                .bodyToFlux(ChatRecord.class);

            List<ChatRecord> records = response.collect(Collectors.toList())
                .share().block();

            model.addAttribute("roomId", roomId);
            model.addAttribute("roomName", roomName);
            model.addAttribute("user", user);
            model.addAttribute("records", records);

        } catch (CustomThrowableException e) {
            log.info(e.getErrorResponse().getCode());
            log.info(e.getErrorResponse().getMessage());
            return "users/chat";
        }

        return "users/chat";

//
//
//        // Participant에서 Room참여자들 정보를 가져와 모델에 전달
//        Participant findParticipant = userService.findByRoomIdAndUserId(roomId,user.getUserId());
//        model.addAttribute("user", user);
//
//        // 유저가 참여하고있는 채팅방정보를 DTO에 담아 모델에 전달
//        RoomInfoDTO roomInfoDTO = new RoomInfoDTO();
//        roomInfoDTO.setName(findParticipant.getRoomName());
//        roomInfoDTO.setRoomId(roomId);
//        model.addAttribute("room", roomInfoDTO);
//
//        // 입장한 채팅방의 채팅메세지들을 가져와 모델에 전달
//        List<Chatting> findChattings = chatService.findAllByRoomId(roomId);
//        model.addAttribute("records",findChattings);
//
//        return "/users/chat";
    }


}
