package com.example.springsecurityapplication.repositories;

import com.example.springsecurityapplication.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByLogin(String login);
    //поиск по паролю
    Optional<Person> findByPassword(String password);

    //запрос для обновления пароля при его смене
    @Modifying
    @Query(value = "UPDATE person set password = ?2 where id=?1", nativeQuery = true)
    void updatePersonById(int id, String password);
}
