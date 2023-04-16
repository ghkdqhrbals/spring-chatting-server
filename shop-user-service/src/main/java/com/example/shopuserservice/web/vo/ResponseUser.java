package com.example.shopuserservice.web.vo;

import com.example.shopuserservice.domain.data.UserTransaction;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUser {
    private String userId;
    private String userName;
    private String email;
    private List<UserTransaction> userTransaction;
    private List<ResponseOrder> orders;
}
