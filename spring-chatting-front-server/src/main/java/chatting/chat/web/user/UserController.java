package chatting.chat.web.user;


import chatting.chat.domain.data.User;
import chatting.chat.web.dto.*;
import chatting.chat.web.error.CustomThrowableException;
import chatting.chat.web.error.ErrorCode;
import chatting.chat.web.error.ErrorResponse;
import chatting.chat.web.filters.cons.SessionConst;
import chatting.chat.web.kafka.dto.CreateChatRoomUnitDTO;
import chatting.chat.web.kafka.dto.RequestChangeUserStatusDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    private WebClient webClient;

    @Value("${backend.api.gateway}")
    private String backEntry;

    @PostConstruct
    public void initWebClient() {
        log.info(backEntry);
        this.webClient = WebClient.create(backEntry);
    }

    // 유저 추가
    @GetMapping
    public String pageAddfriendG(@ModelAttribute("userForm") UserForm form){
        return "users/addUserForm";
    }
    @PostMapping
    public String pageAddfriendP(@Valid @ModelAttribute("userForm") UserForm form, BindingResult bindingResult){

        // Form 에러 모델 전달
        if (bindingResult.hasErrors()){
            return "users/addUserForm";
        }

        try{
            webClient.mutate()
                    .build()
                    .post()
                    .uri("/auth/user")
                    .bodyValue(new RequestAddUserDTO(form.getUserId(),form.getUserPw(),form.getEmail(),form.getUserName()))
                    .retrieve()
                    .onStatus(
                            HttpStatus::is4xxClientError,
                            r -> r.bodyToMono(ErrorResponse.class).map(CustomThrowableException::new))
                    .bodyToMono(User.class)
                    .block();

        }catch (CustomThrowableException e){

            log.info(e.getErrorResponse().getCode());
            log.info(e.getErrorResponse().getMessage());
            if (e.getErrorResponse().getCode().equals(ErrorCode.DUPLICATE_RESOURCE.toString())){
                bindingResult.rejectValue("userId", null, e.getErrorResponse().getMessage());
            }
            return "users/addUserForm";
        }

        return "redirect:/";
    }


    // 유저 상태메세지 변경
    @GetMapping("/status")
    public String updateUserStatus(@ModelAttribute("userStatusUpdateDTO") UserStatusUpdateDTO form){
        return "users/updateStatusForm";
    }
    @PostMapping("/status")
    public String updateUserStatusForm(HttpSession session,
                                       @Valid @ModelAttribute("userStatusUpdateDTO") UserStatusUpdateDTO form,
                                       BindingResult bindingResult){
        User user = (User) session.getAttribute(SessionConst.LOGIN_MEMBER);

        // 상태 메세지 변경
        try{
            webClient.mutate()
                    .build()
                    .post()
                    .uri("/chat/status")
                    .bodyValue(new RequestChangeUserStatusDTO(user.getUserId(),form.getStatusMessage()))
                    .retrieve()
                    .onStatus(
                            HttpStatus::is4xxClientError,
                            r -> r.bodyToMono(ErrorResponse.class).map(CustomThrowableException::new))
                    .bodyToMono(String.class)
                    .block();

        }catch (CustomThrowableException e){
            log.info(e.getErrorResponse().getCode());
            log.info(e.getErrorResponse().getMessage());
            return "users/updateStatusForm";
        }

        return "redirect:/";
    }

    //채팅방 목록 조회
    @GetMapping(value = "/rooms")
    public String rooms(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = true) User user, HttpSession session, Model model){
        try{
            Flux<ChatRoomDTO> response = webClient.mutate()
                    .build()
                    .get()
                    .uri("/chat/rooms?userId=" + user.getUserId())
                    .retrieve()
                    .onStatus(
                            HttpStatus::is4xxClientError,
                            r -> r.bodyToMono(ErrorResponse.class).map(CustomThrowableException::new))
                    .bodyToFlux(ChatRoomDTO.class);
            List<ChatRoomDTO> readers = response.collect(Collectors.toList())
                    .share().block();

            model.addAttribute("list",readers);

        }catch (CustomThrowableException e){

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
    @GetMapping("/room")
    public String createRoom(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = true) User user,
                             @ModelAttribute("form") RoomCreationDTO form, HttpSession session, Model model){

        try{
            Flux<ResponseGetFriend> response = webClient.mutate()
                    .build()
                    .get()
                    .uri("/chat/friend?userId=" + user.getUserId())
                    .retrieve()
                    .onStatus(
                            HttpStatus::is4xxClientError,
                            r -> r.bodyToMono(ErrorResponse.class).map(CustomThrowableException::new))
                    .bodyToFlux(ResponseGetFriend.class);
            List<ResponseGetFriend> readers = response.collect(Collectors.toList()).share().block();

            if (readers.size()>0) {
                form.setFriends(
                        readers
                                .stream()
                                .map(f -> new CreateChatRoomUnitDTO(f.getFriendId(),f.getFriendName(), false))
                                .collect(Collectors.toList())
                );
            }

        }catch (CustomThrowableException e){
            log.info(e.getErrorResponse().getCode());
            log.info(e.getErrorResponse().getMessage());
            return "users/room";
        }

        return "users/room";
    }

    //채팅방 개설
    @PostMapping(value = "/room")
    public String createRoomForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = true) User user,
                                 @ModelAttribute("form") RoomCreationDTO form, HttpSession session, Model model){

        List<String> friendIds = new ArrayList<>();
        for (CreateChatRoomUnitDTO f : form.getFriends()){
            if (f.getJoin()){
                log.info(f.getUserName());
                log.info(f.getUserId());
                log.info(f.getJoin().toString());
                friendIds.add(f.getUserId());
            }else{
                log.info(f.getUserName());
                log.info(f.getUserId());
                log.info(f.getJoin().toString());
            }

        }


        try{
            Flux<ChatRoomDTO> response = webClient.mutate()
                    .build()
                    .post()
                    .uri("/chat/room")
                    .bodyValue(new RequestAddChatRoomDTO(user.getUserId(), friendIds))
                    .retrieve()
                    .onStatus(
                            HttpStatus::is4xxClientError,
                            r -> r.bodyToMono(ErrorResponse.class).map(CustomThrowableException::new))
                    .bodyToFlux(ChatRoomDTO.class);

            List<ChatRoomDTO> readers = response.collect(Collectors.toList())
                    .share().block();


        }catch (CustomThrowableException e){
            log.info(e.getErrorResponse().getCode());
            log.info(e.getErrorResponse().getMessage());
            return "users/room";
        }

        return "redirect:/user/rooms";
    }

    // 채팅방
    @GetMapping("/chat")
    public String chattingRoom(@RequestParam Long roomId, Model model, HttpSession session) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_MEMBER);

        // Participant에서 Room참여자들 정보를 가져와 모델에 전달
        Participant findParticipant = userService.findByRoomIdAndUserId(roomId,user.getUserId());
        model.addAttribute("user", user);

        // 유저가 참여하고있는 채팅방정보를 DTO에 담아 모델에 전달
        RoomInfoDTO roomInfoDTO = new RoomInfoDTO();
        roomInfoDTO.setName(findParticipant.getRoomName());
        roomInfoDTO.setRoomId(roomId);
        model.addAttribute("room", roomInfoDTO);

        // 입장한 채팅방의 채팅메세지들을 가져와 모델에 전달
        List<Chatting> findChattings = chatService.findAllByRoomId(roomId);
        model.addAttribute("records",findChattings);

        return "/users/chat";
    }





}
