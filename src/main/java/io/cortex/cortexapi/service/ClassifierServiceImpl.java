package io.cortex.cortexapi.service;

import io.cortex.cortexapi.db_models.Classifier;
import io.cortex.cortexapi.repository.ClassifierRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClassifierServiceImpl implements ClassifierService {

    @Autowired
    ClassifierRepo classifierRepo;

    @Override
    public Classifier findClassifierByKey(String model_key) {
        return classifierRepo.findClassifierByKey(model_key);
    }

    @Override
    public Classifier findClassifierByTitle(String title) {
        return classifierRepo.findClassifierByTitle(title);
    }

    @Override
    public void save(Classifier classifier) {
        classifierRepo.save(classifier);
    }
}
