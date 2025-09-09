package com.ejemplo.demo.domain.entities.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ejemplo.demo.domain.entities.UserEntity;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

}