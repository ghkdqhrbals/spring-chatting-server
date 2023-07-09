package com.example.shopuserservice.domain.user.repository;

import com.example.commondto.error.CustomException;
import com.example.shopuserservice.domain.data.User;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static com.example.commondto.error.ErrorCode.CANNOT_FIND_USER;
import static com.example.commondto.error.ErrorCode.DUPLICATE_RESOURCE;


@Slf4j
@Repository
public class UserRepositoryJDBC {
    private final JdbcTemplate jdbcTemplate;

    private final HikariDataSource hikariDataSource;

    private int batchSize = 50;

    public UserRepositoryJDBC(JdbcTemplate jdbcTemplate, HikariDataSource hikariDataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.hikariDataSource = hikariDataSource;
    }

    public void saveAll2(List<User> users) {
        try{
            String sql = "INSERT INTO user_table (user_id, email, join_date, login_date, logout_date, user_name, user_pw, role) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?) ";

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
                        ps.setString(8,user.getRole());
                    });
        } catch(Exception e){
            if (e.getClass() == DuplicateKeyException.class){
                throw new CustomException(DUPLICATE_RESOURCE);
            }
            throw new RuntimeException();
        }
    }

    public CompletableFuture<?> saveAll(List<User> users) {
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

        }).exceptionally(e->{
            if (e.getCause().getClass() == DuplicateKeyException.class){
                throw new CustomException(DUPLICATE_RESOURCE);
            }
            throw new RuntimeException();
        });
    }

    // Transaction 과 Connection, SQL 쿼리문, 스레드 모두 직접 컨트롤
    public CompletableFuture<?> login(String user_id, String user_pw) throws CustomException{
        return CompletableFuture.supplyAsync(()->{
            Connection conn = null;
            try {
                // Set "ThreadLocal" connection
                conn = hikariDataSource.getConnection();
                conn.setAutoCommit(false);
                // Repository Logics
                loginUser(user_id,user_pw, conn); // 지저분하긴 하지만, connection 을 그대로 넘겨줌.

                // If everything is fine, Set "ThreadLocal" connection
                conn.commit();
            } catch (Exception e){
                if (conn != null) try {conn.close();} catch (SQLException ex) { throw new RuntimeException(ex); }
                if (e instanceof CustomException) {throw (CustomException) e;}
            } finally {
                if (conn != null) try {conn.close();} catch (SQLException e) {throw new RuntimeException(e);}
            }
            return true;
        });
    }

    public CompletableFuture<?> logout(String user_id, String user_pw) throws CustomException{
        return CompletableFuture.supplyAsync(()->{
            Connection conn = null;
            try {
                // Set "ThreadLocal" connection
                conn = hikariDataSource.getConnection();
                conn.setAutoCommit(false);

                // Repository Logics
                logoutUser(user_id,user_pw, conn);

                // If everything is fine, Set "ThreadLocal" connection
                conn.commit();
            } catch (Exception e){
                if (conn != null) try {conn.close();} catch (SQLException ex) { throw new RuntimeException(ex); }
                if (e instanceof CustomException) {throw (CustomException) e;}
            } finally {
                if (conn != null) try {conn.close();} catch (SQLException e) {throw new RuntimeException(e);}
            }
            return true;
        });
    }

    public void loginUser(String user_id, String user_pw, Connection conn) throws CustomException, SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql1 = String.format("SELECT count(*) c FROM user_table WHERE user_id = \'%s\' AND user_pw = \'%s\' OFFSET 0 LIMIT 1",user_id,user_pw);
        String sql2 = String.format("UPDATE user_table SET login_date =\'%s\' WHERE user_id = \'%s\'",Timestamp.valueOf(LocalDateTime.now()),user_id);
        try{
            // [SQL-1] Search user
            pstmt = conn.prepareStatement(sql1);
            rs = pstmt.executeQuery();
            int count = 0;
            while (rs.next()){
                count= rs.getInt("c");
            }

            // Throw error if not exist
            if (count==0){
                throw new CustomException(CANNOT_FIND_USER);
            }

            // [SQL-2] Update user's login_date
            pstmt = conn.prepareStatement(sql2);
            pstmt.execute();

        } catch (CustomException e){
            throw e;
        } finally {
            assert rs != null;
            rs.close();
            assert pstmt != null;
            pstmt.close();
        }
    }

    public void logoutUser(String user_id, String user_pw, Connection conn) throws CustomException, SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql1 = String.format("SELECT count(*) c FROM user_table WHERE user_id = \'%s\' AND user_pw = \'%s\' OFFSET 0 LIMIT 1",user_id,user_pw);
        String sql2 = String.format("UPDATE user_table SET logout_date =\'%s\' WHERE user_id = \'%s\'",Timestamp.valueOf(LocalDateTime.now()),user_id);
        try{
            // [SQL-1] Search user
            pstmt = conn.prepareStatement(sql1);
            rs = pstmt.executeQuery();
            int count = 0;
            while (rs.next()){
                count= rs.getInt("c");
            }

            // Throw error if not exist
            if (count==0){
                throw new CustomException(CANNOT_FIND_USER);
            }

            // [SQL-2] Update user's login_date
            pstmt = conn.prepareStatement(sql2);
            pstmt.execute();

        } catch (CustomException e){
            throw e;
        } finally {
            assert rs != null;
            rs.close();
            assert pstmt != null;
            pstmt.close();
        }
    }

    @Transactional
    public CompletableFuture<Boolean> logoutUser(String user_id, String user_pw) throws CustomException {
        log.info("currentTransaction:"+TransactionSynchronizationManager.getCurrentTransactionName());

        return CompletableFuture.supplyAsync(()->{
            PreparedStatement pstmt = null;
            Connection conn = null; // local로 설정해야됩니다. Heap에 저장해버리면 Hikari가 활용 X
            boolean rs = false;
            PreparedStatementCreator creator = new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement updateSales = con.prepareStatement(
                            "SELECT count(*) FROM user_table WHERE user_id = ? AND user_pw = ? OFFSET 0 LIMIT 1");
                    updateSales.setString(1, user_id);
                    updateSales.setString(2, user_pw);
                    updateSales.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                    updateSales.setString(4, user_id);
                    return updateSales;
                }
            };

            String sql = String.format("SELECT count(*) c FROM user_table WHERE user_id = \'%s\' AND user_pw = \'%s\' OFFSET 0 LIMIT 1",user_id,user_pw);
            String sql2 = String.format("UPDATE user_table SET logout_date =\'%s\' WHERE user_id = \'%s\'",Timestamp.valueOf(LocalDateTime.now()),user_id);

            try {
                conn = hikariDataSource.getConnection();

                pstmt = conn.prepareStatement(sql);
                ResultSet rs1 = pstmt.executeQuery();
                int count = 0;
                while (rs1.next()){
                    count= rs1.getInt("c");
                }
                if (count==0){
                    throw new CustomException(CANNOT_FIND_USER);
                }

                pstmt = conn.prepareStatement(sql2);
                pstmt.execute();
                return true;
            } catch (SQLException e) {
                try {
                    assert conn != null;
                    conn.rollback();
                } catch (SQLException ex) {throw new RuntimeException(ex);}
                throw new RuntimeException(e);
            } finally {
                if (rs) {
                    try { conn.commit(); } catch (SQLException e) {throw new RuntimeException(e);}
                }
                if(pstmt != null) try { pstmt.close();} catch(SQLException ex) {}
                if(conn != null) try {
                    conn.close();
                    log.info(printHikariCPInfo()+" after release connection");
                } catch(SQLException ex) {}
            }
        }).exceptionally(e->{
            log.info(e.getMessage());
            if (e.getCause().getClass() == CustomException.class){
                log.info("CustomException");
                throw new CustomException(CANNOT_FIND_USER);
            }
            throw new RuntimeException();
        });
    }


    @Transactional
    public CompletableFuture<Boolean> updateLoginDateOfUser(String user_id) throws CustomException {

        log.info("currentTransaction:"+TransactionSynchronizationManager.getCurrentTransactionName());

        return CompletableFuture.supplyAsync(()->{
            PreparedStatement pstmt = null;
            Connection conn = null; // local로 설정해야됩니다. Heap에 저장해버리면 Hikari가 활용 X
            boolean rs = false;
            PreparedStatementCreator creator = new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement updateSales = con.prepareStatement(
                            "UPDATE user_table SET login_date =? WHERE user_id = ?");
                    updateSales.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                    updateSales.setString(2, user_id);
                    return updateSales;
                }
            };

            try {
                log.info(printHikariCPInfo()+" before get connection");
                conn = hikariDataSource.getConnection();
                log.info(printHikariCPInfo()+" after get connection");
                pstmt = creator.createPreparedStatement(conn);
                rs = pstmt.execute();
                return true;
            } catch (SQLException e) {
                try {
                    assert conn != null;
                    conn.rollback(); } catch (SQLException ex) {throw new RuntimeException(ex);}
                throw new RuntimeException(e);
            } catch (CustomException customException){
                try {
                    assert conn != null;
                    conn.rollback(); } catch (SQLException ex) {throw new RuntimeException(ex);}
                throw customException;
            } finally {
                if (rs) {
                    try { conn.commit(); } catch (SQLException e) {throw new RuntimeException(e);}
                }
                if(pstmt != null) try { pstmt.close();} catch(SQLException ex) {}
                if(conn != null) try {
                    conn.close();
                    log.info(printHikariCPInfo()+" after release connection");
                } catch(SQLException ex) {}
            }
        }).exceptionally(e->{
            log.info(e.getMessage());
            if (e.getCause().getClass() == CustomException.class){
                throw new CustomException(CANNOT_FIND_USER);
            }
            throw new RuntimeException();
        });
    }

    private String printHikariCPInfo() {
        return String.format("HikariCP[Total:%s, Active:%s, Idle:%s, Wait:%s]",
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getTotalConnections()),
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getActiveConnections()),
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getIdleConnections()),
                String.valueOf(hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection())
        );
    }


}

