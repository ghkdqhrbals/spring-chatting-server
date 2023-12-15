package chatting.chat.domain.home;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class HealthCheckController {
    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }
}
