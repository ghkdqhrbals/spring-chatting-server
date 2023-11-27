package chatting.chat.Integration;

import chatting.chat.domain.user.repository.UserRepository;
import chatting.chat.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringBootTest
@WebAppConfiguration
public class Integration {
    @SpyBean
    protected UserService userService;
    @SpyBean
    protected UserRepository userRepository;

    @BeforeAll
    public void setUp() {
        userRepository.deleteAll();
    }
}
