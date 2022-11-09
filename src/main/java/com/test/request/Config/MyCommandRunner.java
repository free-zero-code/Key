package com.test.request.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 配置自动启动浏览器
 */

@Component
public class MyCommandRunner implements CommandLineRunner {

    @Value("${server.port}")
    private String port;

    @Override
    public void run(String... args) {
        try {
            Runtime.getRuntime().exec("cmd   /c   start   http://localhost:" + port);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
