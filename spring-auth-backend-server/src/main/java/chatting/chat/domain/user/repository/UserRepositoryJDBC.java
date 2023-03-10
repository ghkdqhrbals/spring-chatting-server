package chatting.chat.domain.user.repository;

import chatting.chat.domain.data.User;
import chatting.chat.web.error.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static chatting.chat.web.error.ErrorCode.DUPLICATE_RESOURCE;

@Slf4j
@Repository
public class UserRepositoryJDBC {
    private final JdbcTemplate jdbcTemplate;
    private final Executor databaseExecutor;

    private int batchSize = 50;

    public UserRepositoryJDBC(JdbcTemplate jdbcTemplate, @Qualifier("taskExecutorForDB") Executor databaseExecutor) {
        this.jdbcTemplate = jdbcTemplate;
        this.databaseExecutor = databaseExecutor;
    }

    @Transactional
    public CompletableFuture<?> saveAll(List<User> users) throws CustomException {
        return CompletableFuture.runAsync(()->{
            String sql = "INSERT INTO user_table (user_id, email, join_date, login_date, logout_date, user_name, user_pw) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) ";

            jdbcTemplate.batchUpdate(sql,
                    users,
                    batchSize,
                    (PreparedStatement ps, User user) -> {
                        ps.setString(1, user.getUserId());
                        ps.setString(2, user.getEmail());
                        ps.setObject(3, user.getJoinDate());
                        ps.setObject(4, user.getLoginDate());
                        ps.setObject(5, user.getLogoutDate());
                        ps.setString(6,user.getUserName());
                        ps.setString(7,user.getUserPw());
                    });

        },databaseExecutor).exceptionally(e->{

            if (e.getCause().getClass() == DuplicateKeyException.class){

                throw new CustomException(DUPLICATE_RESOURCE);
            }
            throw new RuntimeException();
        });
    }
}

