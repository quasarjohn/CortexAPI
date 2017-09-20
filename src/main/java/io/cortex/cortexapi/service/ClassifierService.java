package io.cortex.cortexapi.service;

import io.cortex.cortexapi.db_models.Classifier;
import org.springframework.stereotype.Service;

public interface ClassifierService {

    Classifier findClassifierByKey(String model_key);

    Classifier findClassifierByTitle(String title);

    void save(Classifier classifier);
}
