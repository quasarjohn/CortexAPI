package io.cortex.cortexapi.repository;

import io.cortex.cortexapi.db_models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, String> {

    User findUserByEmail(String email);

    @Query(value = "select * from users where api_key = ?1", nativeQuery = true)
    Iterable<User> findUserByApiKey(String api_key);
}
