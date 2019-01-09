package com.test.dog.dogbreeder.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Model for dog object.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Dog {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long dogId;
    private String breed;
    private String image;
    private Date datePosted;

    @PrePersist
    protected void onCreate() {
        this.datePosted = new Date();
    }
}
