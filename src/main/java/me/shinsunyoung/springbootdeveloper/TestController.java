package me.shinsunyoung.springbootdeveloper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "Spring Boot Application is running! 🎉";
    }

    @GetMapping("/api/test")
    public String apiTest() {
        return "API Test Successful!";
    }

    @GetMapping("/test")
    public String test() {
        return "Test endpoint working!";
    }
}