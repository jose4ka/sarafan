/*
 *  Created by Dmitry Garmyshev on 02.08.2020, 10:45
 *  Copyright (c) 2020 . All rights reserved.
 *  Last modified 02.08.2020, 10:42
 */

/*
Класс конфига для работы с аутентификацией
 */

package com.loakdv.sarafan.config;

import com.loakdv.sarafan.domain.User;
import com.loakdv.sarafan.repo.UserDetailsRepo;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
@EnableOAuth2Sso
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //Сами настройки конфига
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/**")//Основная ветка, на которую распространяется конфиг
                .authorizeRequests()
                .antMatchers("/", "/login**","/js/**", "/error**").permitAll()//Окна которые могут выводиться юзерам
                .anyRequest().authenticated()
                .and().logout().logoutSuccessUrl("/").permitAll()//В каких местах доступен выход из системы
                .and()
                .csrf().disable();
    }

    //Данные о пользователе из Google
    @Bean
    public PrincipalExtractor principalExtractor(UserDetailsRepo userDetailsRepo){
        return map -> {
            String id = (String) map.get("sub");

            //Если пользователя нет в базе, создаём нового и заполняем его данными из гугла
            User user = userDetailsRepo.findById(id).orElseGet(() ->{
                User newUser = new User();

                newUser.setId(id);
                newUser.setName((String) map.get("name"));
                newUser.setEmail((String) map.get("email"));
                newUser.setGender((String) map.get("gender"));
                newUser.setLocale((String) map.get("locale"));
                newUser.setUserpic((String) map.get("picture"));

                return newUser;
            });

            user.setLastVisit(LocalDateTime.now()); //Устанавливаем текущее время

            return userDetailsRepo.save(user);//Сохраняем пользователя в БД
        };
    }


}
