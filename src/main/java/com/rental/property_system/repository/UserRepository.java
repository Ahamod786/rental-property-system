package com.rental.property_system.repository;

import com.rental.property_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // OHMYGAHH Spring automatically knows how to find a user by their email just by reading this method name!
    User findByEmail(String email);
}