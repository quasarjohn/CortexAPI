package io.cortex.cortexapi.repository;

import io.cortex.cortexapi.db_models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, String> {

    User findUserByEmail(String email);
}
