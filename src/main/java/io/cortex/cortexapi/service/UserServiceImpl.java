package io.cortex.cortexapi.service;

import io.cortex.cortexapi.db_models.User;
import io.cortex.cortexapi.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepo userRepo;

    public User findUserByEmail(String email) {
        return userRepo.findUserByEmail(email);
    }
}
