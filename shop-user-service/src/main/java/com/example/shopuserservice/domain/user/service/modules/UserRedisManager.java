package com.example.shopuserservice.domain.user.service.modules;

import com.example.commondto.events.ServiceNames;
import com.example.commondto.events.user.UserResponseEvent;
import com.example.commondto.events.user.UserStatus;
import com.example.shopuserservice.domain.user.data.UserTransactions;
import java.util.Optional;

public class UserRedisManager {

    /**
     * 회원가입 시 UserTransactions에 저장된 UserStatus를 변경합니다.
     * <br>
     * event 의 serviceName에 따라 UserTransactions의 UserStatus를 변경합니다.
     * @param event {@link UserResponseEvent}
     * @param userTransactions {@link UserTransactions}
     */
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
