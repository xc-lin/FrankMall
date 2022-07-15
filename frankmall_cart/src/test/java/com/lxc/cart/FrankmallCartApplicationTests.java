package com.lxc.cart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// @SpringBootTest
class FrankmallCartApplicationTests {

    @Test
    void contextLoads() throws JsonProcessingException {
        com.lxc.cart.vo.Test test = new com.lxc.cart.vo.Test();
        System.out.println(new ObjectMapper().writeValueAsString(test));
    }

}
