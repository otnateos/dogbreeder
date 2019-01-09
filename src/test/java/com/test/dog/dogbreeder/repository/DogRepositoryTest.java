package com.test.dog.dogbreeder.repository;

import com.test.dog.dogbreeder.DogbreederApplication;
import com.test.dog.dogbreeder.model.Dog;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DogbreederApplication.class)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create"
})
public class DogRepositoryTest {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private DogRepository dogRepository;

    private Dog germanShepherd;
    private Dog puddle;

    @Before
    public void setup() {
        this.germanShepherd = new Dog();
        germanShepherd.setBreed("German Shepherd");
        germanShepherd.setImage("image.jpg");
        this.puddle = new Dog();
        puddle.setBreed("Puddle");
        puddle.setImage("cute_image.jpg");
    }

    @Test
    public void testListAll() {
        log.info("Total {}", dogRepository.count());
        dogRepository.findAll().forEach(dog -> log.info("Dog {}", dog));
    }

    @Test
    public void testCreate() {
        long init = dogRepository.count();
        dogRepository.save(germanShepherd);
        assertEquals("Create should increase dog repository size",
                init + 1, dogRepository.count());
    }

    @Test
    public void testReadByDogId() {
        dogRepository.save(germanShepherd);
        assertEquals(germanShepherd.getBreed(), dogRepository.findById(germanShepherd.getDogId()).get().getBreed());
    }

    @Test
    public void testDelete() {
        dogRepository.save(germanShepherd);
        dogRepository.delete(germanShepherd);
        assertFalse(dogRepository.findById(germanShepherd.getDogId()).isPresent());
    }

    @Test
    public void testUpdate() {
        String newBreed = "new_breed";
        dogRepository.save(germanShepherd);
        germanShepherd.setBreed(newBreed);
        dogRepository.save(germanShepherd);
        assertEquals(newBreed, dogRepository.findById(germanShepherd.getDogId()).get().getBreed());
    }

    @Test
    public void testFindDogsByBreed() {
        List<Dog> found = dogRepository.findDogsByBreed(germanShepherd.getBreed());
        int init = found.size();

        dogRepository.save(germanShepherd);
        dogRepository.save(puddle);

        found = dogRepository.findDogsByBreed(germanShepherd.getBreed());
        assertEquals(init + 1, found.size());
        found.stream().allMatch(dog -> germanShepherd.getBreed().equalsIgnoreCase(dog.getBreed()));
    }

    @Test
    public void testGetDogBreeds() {
        dogRepository.save(germanShepherd);
        dogRepository.save(puddle);
        Dog anotherPuddle = new Dog();
        anotherPuddle.setBreed("puddle");
        anotherPuddle.setImage("not_so_cute_image.jpg");
        dogRepository.save(anotherPuddle);

        List<String> breeds = dogRepository.getDogBreeds();
        assertEquals(2, breeds.size());
        assertTrue(breeds.contains(germanShepherd.getBreed()));
        assertTrue(breeds.contains(puddle.getBreed()));
    }
}