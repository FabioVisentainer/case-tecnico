package com.pucpr.casetecnico.backend.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello from Spring Boot");
    }

    @GetMapping("/private/hello")
    public Map<String, String> privateHello(Authentication authentication) {
        return Map.of("message", "Hello " + authentication.getName() + ", this is a protected endpoint");
    }
}
