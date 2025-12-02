package com.ESI.FastFoodESI.repository;

import com.ESI.FastFoodESI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
