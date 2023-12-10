package chatting.chat.web.room;

import chatting.chat.domain.data.User;
import chatting.chat.web.dto.ChatRoomDTO;
import chatting.chat.web.dto.RequestAddChatRoomDTO;
import chatting.chat.web.dto.RoomCreationDTO;
import chatting.chat.web.error.CustomThrowableException;
import chatting.chat.web.error.ErrorResponse;
import chatting.chat.web.filters.cons.SessionConst;
import chatting.chat.web.friend.service.FriendService;
import chatting.chat.web.global.CommonModel;
import chatting.chat.web.kafka.dto.CreateChatRoomUnitDTO;
import chatting.chat.web.login.util.CookieUtil;
import com.example.commondto.dto.friend.FriendResponse;
import com.example.commondto.dto.friend.FriendResponse.FriendDTO;
import com.example.commondto.error.CustomException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@Slf4j
@AllArgsConstructor
public class RoomController {

    private final WebClient.Builder webClientBuilder;
    private final FriendService friendService;
    private final RoomService roomService;

    @GetMapping(value = "/rooms")
    public Mono<String> rooms(HttpServletRequest request, Model model) {
        CommonModel.addCommonModel(model);
        return roomService.getChatRooms(request)
            .collectList()
            .map(chatRooms -> {
                model.addAttribute("list", chatRooms);
                return "chat/chats";
            });
    }

    @GetMapping("/room")
    public String createRoom(@ModelAttribute("form") RoomCreationDTO form, Model model,
        HttpServletRequest request) {

        CommonModel.addCommonModel(model);

        String accessToken = CookieUtil.getCookie(request, "accessToken");
        String refreshToken = CookieUtil.getCookie(request, "refreshToken");

        if (accessToken == null || refreshToken == null) {
            return "redirect:/login";
        }

        try {
            Flux<FriendDTO> response = friendService.getMyFriends(request);
            List<CreateChatRoomUnitDTO> friendsList = response.map(
                f -> new CreateChatRoomUnitDTO(f.getFriendId(), f.getFriendName(),
                    false)).collect(Collectors.toList()).share().block();
            friendsList.stream().forEach(f -> log.info(f.toString()));

            form.setFriends(friendsList);
//            model.addAttribute("friends", friendsList);

        } catch (Exception e) {
            e.printStackTrace();
            return "chat/newChat";
        }
        return "chat/newChat";
    }

    //채팅방 개설
    @PostMapping(value = "/room")
    public String createRoomForm(@Validated @ModelAttribute("form") RoomCreationDTO form, BindingResult bindingResult, Model model,
        HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return "chat/newChat";
        }

        List<String> friendIds = new ArrayList<>();
        for (CreateChatRoomUnitDTO f : form.getFriends()) {
            log.info("friendName: {}, friendId: {}, isJoin?: {}", f.getUserName(),
                f.getUserId(), f.getJoin().toString());
            if (f.getJoin()) {
                friendIds.add(f.getUserId());
            }
        }

        try {
            Flux<ChatRoomDTO> response = webClientBuilder.build()
                .post()
                .uri("/chat/room")
                .bodyValue(new RequestAddChatRoomDTO(friendIds))
                .cookies(c -> {
                    c.add("accessToken", CookieUtil.getCookie(request, "accessToken"));
                    c.add("refreshToken", CookieUtil.getCookie(request, "refreshToken"));
                })
                .retrieve()
                .onStatus(
                    HttpStatus::is4xxClientError,
                    r -> r.bodyToMono(ErrorResponse.class).map(CustomThrowableException::new))
                .bodyToFlux(ChatRoomDTO.class);

            List<ChatRoomDTO> readers = response.collect(Collectors.toList())
                .share().block();


        } catch (CustomThrowableException e) {
            log.info(e.getErrorResponse().getCode());
            log.info(e.getErrorResponse().getMessage());
            return "chat/newChat";
        }

        return "redirect:/";
    }

}
