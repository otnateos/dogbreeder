package com.test.dog.dogbreeder.repository;

import com.test.dog.dogbreeder.model.Dog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository for Dog.
 */
public interface DogRepository extends CrudRepository<Dog, Long> {

    @Query("SELECT dog FROM Dog dog WHERE LOWER(dog.breed) = LOWER(:breedName)")
    List<Dog> findDogsByBreed(String breedName);

    @Query("SELECT DISTINCT dog.breed FROM Dog dog GROUP BY dog.breed")
    List<String> getDogBreeds();
}
