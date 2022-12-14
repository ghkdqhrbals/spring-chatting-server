package chatting.chat.domain.chat;


import chatting.chat.domain.data.Chatting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class ChatService {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public List<Chatting>  findAllByRoomId(Long roomId){
        return chatRepository.findAllByRoomId(roomId);
    }

    public void saveAll(List<Chatting> chattings){
        chatRepository.saveAll(chattings);
    }


}
