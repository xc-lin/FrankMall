package com.lxc.frankmall.search.contoller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Frank_lin
 * @date 2022/6/28
 */
@Controller
public class SeacherController {
    @GetMapping("/list.html")
    public String list(){
        return "list";
    }
}
