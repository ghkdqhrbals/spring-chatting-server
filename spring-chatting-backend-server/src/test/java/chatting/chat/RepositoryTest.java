//package chatting.chat;
//
//
//import chatting.chat.domain.data.Friend;
//import chatting.chat.domain.data.User;
//import chatting.chat.domain.friend.repository.FriendRepository;
//import chatting.chat.domain.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.MockitoJUnitRunner;
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyString;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//public class RepositoryTest {
//
//    @Mock
//    private UserRepository mockedUserRepository;
//    @Mock
//    private FriendRepository mockedFriendRepository;
//
//    @BeforeEach
//    void setup() throws Exception{
//        MockitoAnnotations.initMocks(this);
//    }
//
//
//    @Test
//    @DisplayName("유저저장")
//    public void addUser() {
//        User user = new User("a","A","a");
//
//        Mockito.when(mockedUserRepository.findByUserId(anyString())).thenReturn(user);
//
//        assertThat(user.getUserId()).isEqualTo("a");
////
////        assertThat(a).isNotNull();
//    }
//
////    @Test
////    @DisplayName("유저삭제")
////    public void removeUser() {
////        User user = new User("a","a","a");
////        mockedUserRepository.save(user);
////        User a = mockedUserRepository.findByUserId("a");
////        assertThat(a).isNotNull();
////
////        mockedUserRepository.delete(user);
////        User a1 = mockedUserRepository.findByUserId("a");
////        assertThat(a1).isNull();
////    }
////
////    @Test
////    @DisplayName("친구 저장")
////    public void addFriend() {
////        User user1 = new User("a","a","a");
////        User user2 = new User("b","b","b");
////        mockedUserRepository.save(user1);
////        mockedUserRepository.save(user2);
////
////        Friend friend = new Friend(user1, user2.getUserId());
////        mockedFriendRepository.save(friend);
////
////        Friend findFriend = mockedFriendRepository.findByUserIdAndFriendId(user1.getUserId(), user2.getUserId());
////
////        assertThat(findFriend).isNotNull();
////        assertThat(findFriend.getUser()).isEqualTo(user1);
////        assertThat(findFriend.getUser().getUserId()).isEqualTo(user1.getUserId());
////        assertThat(findFriend.getFriendId()).isEqualTo(user2.getUserId());
////    }
////
////    @Test
////    @DisplayName("친구 삭제")
////    public void removeFriend() {
////        User user1 = new User("a","a","a");
////        User user2 = new User("b","b","b");
////        User save = mockedUserRepository.save(user1);
////        User save1 = mockedUserRepository.save(user2);
////
////        Friend friend = new Friend(user1, user2.getUserId());
////        mockedFriendRepository.save(friend);
////
////        Friend findFriend = mockedFriendRepository.findByUserIdAndFriendId(user1.getUserId(), user2.getUserId());
////
////        mockedFriendRepository.delete(findFriend);
////
////        Friend findFriend2 = mockedFriendRepository.findByUserIdAndFriendId(user1.getUserId(), user2.getUserId());
////
////        assertThat(findFriend2).isNull();
////    }
//}