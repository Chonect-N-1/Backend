package com.snapshot.chonect.domain.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snapshot.chonect.domain.models.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    // aqui adentro se van a implementar los metodos personalizados
    boolean existsByUsername(String username);

    // el findByUsername no los veo tan necesarios pero igual los tengo
    Optional<UserEntity> findByUsername(String username);
    Optional <UserEntity> findByEmail(String email);
    UserEntity getByEmail(String email);

    Optional <UserEntity> findByUsernameOrEmail(String username, String email);
    Optional <UserEntity> findByVerificationCode(String verificationCode);

}
