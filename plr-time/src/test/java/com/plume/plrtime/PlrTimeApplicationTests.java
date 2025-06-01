package com.plume.plrtime;

import com.plume.plrtime.common.CustomMd5PasswordEncoder;
import com.plume.plrtime.service.StatisticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;

@SpringBootTest
class PlrTimeApplicationTests {

    @Autowired
    private CustomMd5PasswordEncoder customMd5PasswordEncoder;
    @Autowired
    private StatisticsService statisticsService;


    @Test
     void test1() {
        System.out.println(statisticsService.getActivityDurationsByDate(LocalDate.now()));
    }

    @Test
    void contextLoads() {
        String encode = customMd5PasswordEncoder.encode("123456");
        System.out.println(encode.toString());
    }

    @Test
    void test() {
//        String encode = "[-31, 10, -36, 57, 73, -70, 89, -85, -66, 86, -32, 87, -14, 15, -120, 62]";
//        System.out.println(customMd5PasswordEncoder.matches("123456", encode));
        String rawPassword = "123456";
        String encode = customMd5PasswordEncoder.encode(rawPassword);
        System.out.println("加密:"+encode);
        System.out.println(customMd5PasswordEncoder.matches(rawPassword, encode));

//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//
//        // 1. 加密
//        String password = "admin123";
//        String encodedPassword = encoder.encode(password);
//        System.out.println("加密结果: " + encodedPassword);
//
//        // 2. 验证
//        boolean isMatch = encoder.matches("admin123", encodedPassword);
//        System.out.println("密码验证: " + isMatch); // true
//
//        // 3. 错误密码测试
//        isMatch = encoder.matches("wrongPwd", encodedPassword);
//        System.out.println("错误密码验证: " + isMatch); // false
    }

}
