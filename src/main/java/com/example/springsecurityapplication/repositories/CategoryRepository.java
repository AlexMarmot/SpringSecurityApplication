package com.example.springsecurityapplication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryRepository,Integer> {
    com.example.springsecurityapplication.models.Category findByName(String name);
}
