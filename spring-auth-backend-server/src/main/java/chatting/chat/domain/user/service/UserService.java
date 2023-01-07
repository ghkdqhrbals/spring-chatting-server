package chatting.chat.domain.user.service;

import chatting.chat.domain.data.User;


public interface UserService {
    User save(User item);
    User findById(String id);
    User login(String userId, String userPw);
    void logout(String userId);
    void removeUser(String userId);
    void updateUser(User user);

}
