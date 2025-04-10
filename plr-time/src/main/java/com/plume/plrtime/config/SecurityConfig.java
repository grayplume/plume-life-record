package com.plume.plrtime.config;


import com.plume.plrtime.common.CustomMd5PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;



@Configuration
/**
 * @EnableWebSecurity是开启SpringSecurity的默认行为
 */
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 密码明文加密方式配置
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new CustomMd5PasswordEncoder();
    }

    /**
     * 获取AuthenticationManager（认证管理器），登录时认证使用
     *
     * @param authenticationConfiguration
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 开启授权保护
                .authorizeHttpRequests(authorize -> authorize
                        // 不需要认证的地址有哪些
                        .requestMatchers("/web/index", "/public/**", "/about").permitAll()	// ** 通配符
                        // 对所有请求开启授权保护
                        .anyRequest().
                        // 已认证的请求会被自动授权
                                authenticated()
                )
                // 使用默认的登陆登出页面进行授权登陆
                //  .formLogin(Customizer.withDefaults());

                .formLogin( form -> {
                    form
                            .loginPage("/web/login").permitAll() //登录页面无需授权即可访问
                            .defaultSuccessUrl("/web/index",true)
                            .usernameParameter("username") //自定义表单用户名参数，默认是username
                            .passwordParameter("password") //自定义表单密码参数，默认是password

                    ;
                }); //使用表单授权方式
                // 启用“记住我”功能的。允许用户在关闭浏览器后，仍然保持登录状态，直到他们主动注销或超出设定的过期时间。
        // .rememberMe(Customizer.withDefaults());
        // 关闭 csrf CSRF（跨站请求伪造）是一种网络攻击，攻击者通过欺骗已登录用户，诱使他们在不知情的情况下向受信任的网站发送请求。
        http.csrf(AbstractHttpConfigurer::disable);

        //会话管理
        http.sessionManagement(session -> {
            session
                    .maximumSessions(1)
                    .expiredSessionStrategy(new MySessionInformationExpiredStrategy());
        });
        return http.build();
    }


    /**
     * 配置跨源访问(CORS)
     *
     * @return
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}