package io.cortex.cortexapi.service;

import io.cortex.cortexapi.db_models.User;

public interface UserService {

    User findUserByEmail(String email);
}
