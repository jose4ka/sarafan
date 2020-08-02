/*
 *  Created by Dmitry Garmyshev on 02.08.2020, 10:45
 *  Copyright (c) 2020 . All rights reserved.
 *  Last modified 01.08.2020, 23:47
 */

/*
Главный контроллер возвращающий пользователя и его сообщения
 */
package com.loakdv.sarafan.controller;

import com.loakdv.sarafan.domain.User;
import com.loakdv.sarafan.repo.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

@Controller
@RequestMapping("/")
public class MainController {

    private final MessageRepo messageRepo;

    @Autowired
    public MainController(MessageRepo messageRepo){
        this.messageRepo = messageRepo;
    }

    @GetMapping
    public String main(Model model, @AuthenticationPrincipal User user){
        HashMap<Object, Object> data = new HashMap<>();
        data.put("profile", user);
        data.put("messages", messageRepo.findAll());
        model.addAttribute("frontendData", data);
        return "index";
    }
}
