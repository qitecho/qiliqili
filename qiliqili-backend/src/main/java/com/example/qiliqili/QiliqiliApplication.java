package com.example.qiliqili;

import com.example.qiliqili.im.IMServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling   // 启用定时任务
public class QiliqiliApplication {

    public static void main(String[] args) {
        SpringApplication.run(QiliqiliApplication.class, args);
        new Thread(() -> {
            try {
                new IMServer().start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
