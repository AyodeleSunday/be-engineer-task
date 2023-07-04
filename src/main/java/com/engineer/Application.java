package com.engineer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author Sunday Ayodele
 * @create 7/2/2023
 */

@SpringBootApplication
@EnableSwagger2
public class Application {
    public static void main(String... args){

        SpringApplication.run(Application.class, args);
    }
}
