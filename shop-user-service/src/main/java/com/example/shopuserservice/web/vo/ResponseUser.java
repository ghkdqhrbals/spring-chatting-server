package com.example.shopuserservice.web.vo;

import com.example.shopuserservice.domain.data.UserTransactions;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.util.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUser {
    private String userId;
    private String userName;
    private String email;
    private List<UserTransactions> userTransaction;
    private List<ResponseOrder> orders;

    @Builder
    public ResponseUser(String userId, String userName, String email, List<UserTransactions> userTransaction, List<ResponseOrder> orders) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.userTransaction = userTransaction;
        this.orders = orders;
    }
}
