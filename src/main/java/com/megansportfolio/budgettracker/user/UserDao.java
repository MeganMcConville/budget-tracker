package com.megansportfolio.budgettracker.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Long> {

    User findOneByUsernameIgnoreCase(String username);

}
