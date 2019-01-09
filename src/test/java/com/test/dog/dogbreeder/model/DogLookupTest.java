package com.test.dog.dogbreeder.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class DogLookupTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testTransformDogLookupPayload()
            throws Exception
    {
        InputStream jobsJson = Test.class.getResourceAsStream("/doglookup.json");
        DogLookup dogLookup = mapper.readValue(jobsJson, DogLookup.class);
        assertEquals(dogLookup.getStatus(), "success");
        assertEquals(dogLookup.getMessage(), "https://images.dog.ceo/breeds/entlebucher/n02108000_1891.jpg");
    }
}
