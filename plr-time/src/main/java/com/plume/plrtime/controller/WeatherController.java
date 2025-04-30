package com.plume.plrtime.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.zip.GZIPInputStream;

@RestController
public class WeatherController {

    @Value("${weather.api-key}")
    private String weatherApiKey;

    @Value("${weather.base-url}")
    private String weatherBaseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/getWeatherByLocation")
    public String getWeatherByLocation(
            @RequestParam double lat,
            @RequestParam double lon) throws IOException {

        String url = String.format("%s/geo/v2/city/lookup?location=%f,%f", weatherBaseUrl, lon, lat);

        // 1. 构造请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-QW-Api-Key", weatherApiKey);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept-Charset", "UTF-8");
        headers.set("Accept-Encoding", "gzip");

        // 2. 封装成 HttpEntity
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // 3. 发起带 Header 的 GET 请求
        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                byte[].class
        );

        // 4. 解压 Gzip 响应体
        byte[] compressedData = response.getBody();
        if (compressedData != null) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedData);
            GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
            byte[] decompressedData = gzipInputStream.readAllBytes();
            String jsonResponse = new String(decompressedData, StandardCharsets.UTF_8); // 解压后的 JSON 数据

            // 5. 解析 JSON 获取 id
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode locationNode = rootNode.path("location").get(0); // 获取第一个 location 对象
            String id = locationNode.path("id").asText(); // 获取 id
            String name = locationNode.path("name").asText(); // 获取 name

            // 返回 id 给前端
//            return id;
            return getWeather(id);
        }
        return null;
    }


    public String getWeather(@RequestParam String locationId) throws IOException {
        // 构建获取实时天气的URL
        String url = String.format("%s/v7/weather/3d?location=%s", weatherBaseUrl, locationId);

        // 1. 构造请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-QW-Api-Key", weatherApiKey);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept-Charset", "UTF-8");
        headers.set("Accept-Encoding", "gzip");

        // 2. 封装成 HttpEntity
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // 3. 发起带 Header 的 GET 请求
        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                byte[].class
        );

        // 4. 解压 Gzip 响应体
        byte[] compressedData = response.getBody();
        if (compressedData != null) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedData);
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
                 Reader reader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8)) {
                // 读取解压后的数据
                String jsonResponse = new String(gzipInputStream.readAllBytes(), StandardCharsets.UTF_8);

                // 5. 解析 JSON 获取天气信息
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(jsonResponse);
//                JsonNode nowNode = rootNode.path("now");
//                // 获取天气数据
//                String weather = nowNode.path("text").asText(); // 天气描述
//                String temp = nowNode.path("temp").asText(); // 温度
//                String windDir = nowNode.path("windDir").asText(); // 风向
//                String windSpeed = nowNode.path("windSpeed").asText(); // 风速
//                String humidity = nowNode.path("humidity").asText(); // 湿度

                //  获取最近几天天气，以及每日的的最高最低温度
                JsonNode dailyNode = rootNode.path("daily");
                // 获取今天数据
                if (dailyNode.isEmpty()) {
                    return "无法获取天气数据";
                }
                JsonNode todayNode = dailyNode.get(0);
                String weather = todayNode.path("textDay").asText();
                String tempMax = todayNode.path("tempMax").asText();
                String tempMin = todayNode.path("tempMin").asText();

                // 6. 返回天气信息
                return String.format("%s %s℃",
                        weather, tempMax+"/"+tempMin);
            }
        }

        return "无法获取天气数据";
    }
}
