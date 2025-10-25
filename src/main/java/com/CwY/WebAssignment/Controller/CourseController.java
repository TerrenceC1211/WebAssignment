package com.CwY.WebAssignment.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CourseController {
    @GetMapping("/course")
    public String displayCoursePage() {
        return "course";
    }
}
