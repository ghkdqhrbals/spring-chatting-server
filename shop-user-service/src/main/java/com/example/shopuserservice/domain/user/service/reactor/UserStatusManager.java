package com.example.shopuserservice.domain.user.service.reactor;

import com.example.commondto.events.user.UserStatus;
import com.example.shopuserservice.domain.user.data.UserTransactions;
import com.example.shopuserservice.web.util.reactor.Reactor;

public class UserStatusManager {
    /**
     * 각 상태가 대기중(APPEND)이 아닌 경우 {@link UserTransactions} 을 {@link Reactor} 의 emit 메서드를 통해 클라이언트에 전송합니다.
     *
     * @param userTransactions {@link UserTransactions}
     * @param userId String
     */
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
