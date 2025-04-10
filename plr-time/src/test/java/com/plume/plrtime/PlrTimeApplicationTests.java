package com.plume.plrtime;

import com.plume.plrtime.common.CustomMd5PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PlrTimeApplicationTests {

    @Autowired
    private CustomMd5PasswordEncoder customMd5PasswordEncoder;

    @Test
    void contextLoads() {
        String encode = customMd5PasswordEncoder.encode("123456");
        System.out.println(encode.toString());
    }

    @Test
    void test() {
        String encode = "[-31, 10, -36, 57, 73, -70, 89, -85, -66, 86, -32, 87, -14, 15, -120, 62]";
        System.out.println(customMd5PasswordEncoder.matches("123456", encode));
    }

}
