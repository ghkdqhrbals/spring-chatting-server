package com.example.commondto.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


public class CustomerRequest {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    public static class NewCustomerDTO {

        private String name;
        private String email;
        private String password;
        private String phone;
        private String address;
    }
}
