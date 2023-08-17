package com.springbootjwt.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springbootjwt.entities.User;

public interface UserRepo extends JpaRepository<User, String>{
	
	public Optional<User> findByEmail(String email);

}
