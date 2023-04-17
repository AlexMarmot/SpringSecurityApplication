package com.example.springsecurityapplication.security;

import com.example.springsecurityapplication.models.Person;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class PersonDetails implements UserDetails {
    private final Person person;

    public PersonDetails(Person person) {
        this.person = person;
    }

    public Person getPerson(){
        return this.person;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(person.getRole()));
    }
    //Возвращает пароль пользователя
    @Override
    public String getPassword() {
        return this.person.getPassword();
    }

    //Возвращает логин пользователя
    @Override
    public String getUsername() {
        return this.person.getLogin();
    }

    //Следующие методы оворят о том, что текущая сущность (в БД) активна, не заблокирована, существуети т.д.:

    // Аккаунт активен(действителен) иили нет
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    // Аккаунт заблокирован или нет
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    // Пароль активен или нет
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    // Аккаунт активен или его деактивировали
    @Override
    public boolean isEnabled() {
        return true;
    }
}
