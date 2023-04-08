package com.example.shopuserservice.client;

import com.example.shopuserservice.web.vo.ResponseOrder;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class OrderServiceClientWithFallback implements OrderServiceClient {

    @Override
    public List<ResponseOrder> getOrders(String userId) {
        log.info("OrderServiceClientWithFallback");
        try {
            throw new NotFoundException("hi, something wrong");
        } catch (Exception ex) {
            log.info(ex.getMessage());
            if (ex instanceof BadRequestException) {
                return null;
            }
            if (ex instanceof NotFoundException) {
                return null;
            }
            if (ex instanceof Exception) {
                log.info("EXCEPTIONS");
                return null;
            }
            return null;
        }
    }
}
