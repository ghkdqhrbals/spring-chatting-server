package chatting.chat.web.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@AllArgsConstructor
public enum SQLErrorCode {
    unique_violation("23505"),
    ;
    private String SQLStateCode;

}
