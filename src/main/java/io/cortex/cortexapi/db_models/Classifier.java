package io.cortex.cortexapi.db_models;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Classifiers")
public class Classifier {

    /*
    I didn't include every property of the classifier as it can be read from the metadata file
    in the classifier folder. The email and the title of the classifier should be enough to
    identify the folder
     */

    @Id
    @Column(name="key", nullable = false, unique = true)
    @NotEmpty
    private String key;

    //reference to the user email
    @Column(name = "email")
    private String email;

    @Column(name = "title")
    private String title;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
