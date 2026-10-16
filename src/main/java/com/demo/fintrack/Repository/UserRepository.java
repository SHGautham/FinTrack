package com.demo.fintrack.Repository;

import com.demo.fintrack.Domain.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
