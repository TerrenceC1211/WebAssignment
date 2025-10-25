package com.CwY.WebAssignment.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BlogController {
    @GetMapping("/blog")
    public String displayBlogPage() {
        return "blog";
    }
}
