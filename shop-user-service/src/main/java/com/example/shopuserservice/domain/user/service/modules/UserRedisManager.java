package com.example.shopuserservice.domain.user.service.modules;

import com.example.commondto.events.ServiceNames;
import com.example.commondto.events.user.UserResponseEvent;
import com.example.commondto.events.user.UserStatus;
import com.example.shopuserservice.domain.user.data.UserTransactions;

public class UserRedisManager {
    public static void changeUserRegisterStatusByEventResponse(UserResponseEvent event, UserTransactions userTransactions) {
        switch (event.getServiceName()){
            case ServiceNames.chat -> {
                userTransactions.setChatStatus(UserStatus.valueOf(event.getUserStatus()));
                break;
            }
            case ServiceNames.customer -> {
                userTransactions.setCustomerStatus(UserStatus.valueOf(event.getUserStatus()));
                break;
            }
        }
    }
}
