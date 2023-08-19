package com.simongig.recipesapp.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {
    @RequestMapping(value = "/{path:^(?!api|css|js)(?!index\\.html$).*}/**")
    public String forward(@PathVariable String path) {
        System.out.println("------ Forward - path: " + path);
        return "forward:/";
    }
}