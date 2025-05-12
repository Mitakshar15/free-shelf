package com.freeshelf.api.data.repository;

import com.freeshelf.api.data.domain.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

  @Query("select u from User u where u.firstName=:userName or u.email=:userName")
  Optional<User> findByEmailOrFirstName(String userName);

  Optional<User> findByEmail(String email);

  boolean existsByUserName(String username);

  boolean existsByEmail(@NotNull @Email String email);

  @Query("select u from User u where u.email=:userName or u.userName=:userName")
  Optional<User> findByEmailOrUserName(@Param("userName") String userName);

}
