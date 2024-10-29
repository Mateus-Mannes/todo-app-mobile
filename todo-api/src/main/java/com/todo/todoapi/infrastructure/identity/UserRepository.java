package com.todo.todoapi.infrastructure.identity;

import com.todo.todoapi.domain.identity.User;
import com.todo.todoapi.infrastructure.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
