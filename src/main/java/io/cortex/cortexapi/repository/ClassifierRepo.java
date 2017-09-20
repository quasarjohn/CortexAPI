package io.cortex.cortexapi.repository;

import io.cortex.cortexapi.db_models.Classifier;
import org.springframework.data.repository.CrudRepository;

public interface ClassifierRepo extends CrudRepository<Classifier, String> {

    Classifier findClassifierByKey(String model_key);
}
