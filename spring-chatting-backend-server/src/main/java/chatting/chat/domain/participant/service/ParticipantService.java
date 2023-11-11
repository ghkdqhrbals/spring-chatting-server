package chatting.chat.domain.participant.service;

import chatting.chat.domain.participant.entity.Participant;

import chatting.chat.domain.user.entity.User;
import java.util.List;



public interface ParticipantService {

    List<Participant> findAllByUserId(String userId);
    List<User> findParticipantByRoomId(Long roomId);

    String save(Participant participant);

    String remove(Participant participant);

}
