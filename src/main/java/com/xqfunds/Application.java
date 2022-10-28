package com.xqfunds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author whisper
 * @date 2022/10/27
 **/
@SpringBootApplication
public class Application {

    // run the SpringApplication from the IntagrationApplication with the specified parameters
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}