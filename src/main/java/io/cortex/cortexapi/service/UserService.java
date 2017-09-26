package io.cortex.cortexapi.service;

import io.cortex.cortexapi.db_models.User;

public interface UserService {

    User findUserByEmail(String email);
    Iterable<User> findUserByApiKey(String api_key);
}
