package com.simongig.recipesapp.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping("/{path:^(?!api).*$}/**")
    public String forward() {
        System.out.println("----- FORWARDED -----");
        return "forward:/";
    }
}
