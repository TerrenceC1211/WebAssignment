package com.CwY.WebAssignment.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InstructorController {
    @GetMapping("/instructor")
    public String displayInstructorPage() {
        return "instructor";
    }
}
