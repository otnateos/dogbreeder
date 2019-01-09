package com.test.dog.dogbreeder;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.test.dog.dogbreeder.model.Dog;
import com.test.dog.dogbreeder.repository.DogRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(DogController.class)
public class DogControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private DogRepository dogRepository;

    @MockBean
    private AmazonS3 amazonS3;

    @InjectMocks
    private DogController dogController;

    private Dog germanShepherd;

    @Before
    public void setUp() {
        this.germanShepherd = new Dog();
        germanShepherd.setBreed("German Shepherd");
        germanShepherd.setImage("image.jpg");
    }

    @Test
    public void testBreed() throws Exception {
        given(amazonS3.putObject(any(PutObjectRequest.class))).willReturn(new PutObjectResult());
        given(amazonS3.getUrl(anyString(), anyString())).willReturn(new URL("https://dog.ceo"));
        mvc.perform(post("/dog/breed"))
                .andExpect(status().isOk());

        verify(dogRepository, atLeastOnce()).save(any(Dog.class));
        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
        verify(amazonS3, times(1)).getUrl(anyString(), anyString());
    }

    @Test
    public void testReadDogById_dogNotFound() throws Exception {
        given(dogRepository.findById(anyLong())).willReturn(Optional.empty());
        mvc.perform(get("/dog/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testReadDogById() throws Exception {
        given(dogRepository.findById(anyLong())).willReturn(Optional.of(germanShepherd));
        mvc.perform(get("/dog/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.breed", is(germanShepherd.getBreed())));
    }

    @Test
    public void testDeleteDogById_dogDoesNotExist() throws Exception {
        given(dogRepository.findById(anyLong())).willReturn(Optional.empty());
        mvc.perform(delete("/dog/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteDogById() throws Exception {
        given(dogRepository.findById(anyLong())).willReturn(Optional.of(germanShepherd));
        mvc.perform(delete("/dog/1"))
                .andExpect(status().isOk());
        verify(dogRepository, times(1)).delete(any(Dog.class));
        verify(amazonS3, times(1)).deleteObject(anyString(), anyString());
    }

    @Test
    public void testGetBreedNames() throws Exception {
        given(dogRepository.findDogsByBreed(anyString())).willReturn(Arrays.asList(germanShepherd));
        mvc.perform(get("/dog/breed/german shepherd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].breed", is(germanShepherd.getBreed())));
    }

    @Test
    public void testGetAllBreeds() throws Exception {
        given(dogRepository.getDogBreeds()).willReturn(Arrays.asList("puddle", "bulldog"));
        mvc.perform(get("/dog/breeds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", is("puddle")));
    }

}
