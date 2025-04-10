package com.plume.plrtime.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class MySessionInformationExpiredStrategy implements SessionInformationExpiredStrategy {
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {

        // 创建结果对象
        Map<String, Object> result = new HashMap<>();
        result.put("code", -1);
        result.put("message", "该账号已从其他设备登录");

        // 手动构建 JSON 字符串
        String json = "{"
                + "\"code\": " + result.get("code") + ","
                + "\"message\": \"" + result.get("message") + "\""
                + "}";

        // 获取响应对象
        HttpServletResponse response = event.getResponse();

        // 设置响应类型为 JSON 并设置 UTF-8 编码
        response.setContentType("application/json;charset=UTF-8");

        // 返回 JSON 响应
        response.getWriter().println(json);
        response.getWriter().flush();
    }


}