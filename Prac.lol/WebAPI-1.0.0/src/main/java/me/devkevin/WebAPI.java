package me.devkevin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;


/**
 * Created by DevKevin
 * Project: me.devkevin.WebAPI
 * Date: 22/02/2022 @ 20:22
 */
@SpringBootApplication
@EntityScan("me.devkevin.model")
@ComponentScan({"me.devkevin.repo", "me.devkevin.model", "me.devkevin.controller"})
public class WebAPI extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(WebAPI.class, args);
    }
}
