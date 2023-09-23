package com.example.shopuserservice.domain.user.service.reactor;

import com.example.commondto.events.user.UserStatus;
import com.example.shopuserservice.domain.user.data.UserTransactions;
import com.example.shopuserservice.web.util.reactor.Reactor;

public class UserStatusManager {
    // send userTransactions to client when each status is not INSERT or DELETE
    public static void sendUserStatusToClient(UserTransactions userTransactions, String userId) {
        if (!userTransactions.getChatStatus().equals(UserStatus.USER_INSERT_APPEND)
                && !userTransactions.getCustomerStatus().equals(UserStatus.USER_INSERT_APPEND)
                && !userTransactions.getUserStatus().equals(UserStatus.USER_INSERT_APPEND)
                && !userTransactions.getUserStatus().equals(UserStatus.USER_DELETE_APPEND)) {
            Reactor.emitAndComplete(userId, userTransactions);
        } else {
            Reactor.emit(userId, userTransactions);
        }
    }
}
