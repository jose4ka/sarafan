/*
 *  Created by Dmitry Garmyshev on 02.08.2020, 10:45
 *  Copyright (c) 2020 . All rights reserved.
 *  Last modified 01.08.2020, 16:03
 */
/*
Контроллер для работы с сообщениями
Тут происходят операции добаления в БД, удаления, получения и т.д.
 */
package com.loakdv.sarafan.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.loakdv.sarafan.domain.Message;
import com.loakdv.sarafan.domain.Views;
import com.loakdv.sarafan.repo.MessageRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("message")
public class MessageController {

    //DAO для работы с хранилищем сообщений
    private final MessageRepo messageRepo;

    @Autowired
    public MessageController(MessageRepo messageRepo){
        this.messageRepo = messageRepo;
    }

    //Получить все сообщения которые есть
    @GetMapping
    @JsonView(Views.IdName.class)
    public List<Message> list(){
        return messageRepo.findAll();
    }

    //Получить отдельное сообщение
    @GetMapping("{id}")
    public Message getOneMessage(@PathVariable("id") Message message){
        return message;
    }

    //Создать сооьщение
    @PostMapping
    public Message create(@RequestBody Message message){
        message.setCreationDate(LocalDateTime.now());
        return messageRepo.save(message);
    }

    //Обновить имеющееся сообщение
    @PutMapping("{id}")
    public Message update(
            @PathVariable("id") Message messageFromDb,
            @RequestBody Message message) {

        BeanUtils.copyProperties(message, messageFromDb, "id");
        return messageRepo.save(messageFromDb);
    }

    //Удалить сообщение
    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") Message message){
        messageRepo.delete(message);
    }

}
