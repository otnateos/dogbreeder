package com.test.dog.dogbreeder;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.test.dog.dogbreeder.exception.BreederNotAvailableException;
import com.test.dog.dogbreeder.exception.DogNotAvailableForDeletionException;
import com.test.dog.dogbreeder.exception.DogNotFoundException;
import com.test.dog.dogbreeder.exception.FailToBreedException;
import com.test.dog.dogbreeder.model.Dog;
import com.test.dog.dogbreeder.model.DogLookup;
import com.test.dog.dogbreeder.repository.DogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/dog")
public class DogController {

    private final Logger log = LoggerFactory.getLogger(DogController.class);
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${dog.source}")
    private String dogSourceUrl;
    @Value("${dog.lookup.pattern}")
    private String dogLookupPattern;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Autowired
    private DogRepository dogRepository;
    @Autowired
    private AmazonS3 amazonS3;

    private Pattern dogBreedPattern;

    private Matcher matchWithPattern(DogLookup dogLookup)
    {
        if (dogBreedPattern == null) {
            this.dogBreedPattern = Pattern.compile(dogLookupPattern);
        }
        return dogBreedPattern.matcher(dogLookup.getMessage());
    }

    @PostMapping("/breed")
    public Dog breed() {
        log.info("Retrieving breed from {}", dogSourceUrl);
        DogLookup dogLookup = restTemplate.getForObject(dogSourceUrl, DogLookup.class);
        if (dogLookup == null) {
            throw new BreederNotAvailableException();
        }
        log.info("Dog lookup result {}", dogLookup);
        if (!dogLookup.success()) {
            throw new FailToBreedException();
        }
        log.info("Ensuring URL is correct");
        Matcher matcher = matchWithPattern(dogLookup);
        if (!matcher.find()) {
            throw new FailToBreedException();
        }
        String image = matcher.group(0);
        log.info("Generated breed {} with image {}", matcher.group(1), image);
        Dog dog = new Dog();
                dog.setBreed(matcher.group(1));
                dog.setImage(matcher.group(2));
        // dogId generator need to be set up from mongo + s3
        log.info("Saving dog to database");
        dogRepository.save(dog);
        log.info("Dog saved with Id: {}", dog.getDogId());

        // store the dog picture in s3
        String key = String.format("%d/%s", dog.getDogId(), dog.getImage());
        try (InputStream imageStream = new URL(image).openStream())
        {
            log.info("Putting object to S3 bucket {} with key {}", bucket, key);
            PutObjectRequest request = new PutObjectRequest(bucket, key, imageStream, new ObjectMetadata())
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            PutObjectResult result = amazonS3.putObject(request);

            log.info("Content MD5 {}", result.getContentMd5());
            String s3url = amazonS3.getUrl(bucket, key).toString();
            log.info("Image saved into S3 url '{}'", s3url);

            log.info("Update image with S3 url");
            dog.setImage(s3url);
            dogRepository.save(dog);
        }
        catch (Exception e)
        {
            log.error("Failed to put object", e);
            throw new FailToBreedException();
        }
        return dog;
    }

    @GetMapping(value = "/{dogId}")
    public Dog get(@PathVariable("dogId") Long id) {
        log.info("Retrieve dog by dogId {}", id);
        Optional<Dog> dog = dogRepository.findById(id);
        if (!dog.isPresent())
        {
            throw new DogNotFoundException();
        }
        log.info("Returning dog {}", dog);
        return dog.get();
    }

    @DeleteMapping("/{dogId}")
    public void delete(@PathVariable("dogId") Long id) {
        log.info("Delete dog by dogId {}", id);
        Optional<Dog> dogOptional = dogRepository.findById(id);
        if (!dogOptional.isPresent())
        {
            throw new DogNotAvailableForDeletionException();
        }
        Dog dog = dogOptional.get();
        String key = String.format("dog/%d/%s", dog.getDogId(), dog.getImage());
        log.info("Deleting dog {}", dog);
        dogRepository.delete(dog);
        log.info("Deleting image {} from S3", key);
        amazonS3.deleteObject(bucket, key);
        log.info("Successfully deleted dog {}", dog);
    }

    @GetMapping("/breed/{breedName}")
    public List<Dog> findByBreeds(@PathVariable("breedName") String breedName) {
        log.info("Find dog by breed name '{}'", breedName);
        List<Dog> dogs = dogRepository.findDogsByBreed(breedName);
        if (dogs.isEmpty())
        {
            throw new DogNotFoundException("No dogs found for breed '" + breedName + "'");
        }
        log.info("Found {} dogs for breed '{}'", dogs.size(), breedName);
        return dogs;
    }

    @GetMapping("/breeds")
    public List<String> getBreedNames() {
        log.info("Get available dog breeds");
        List<String> breeds = dogRepository.getDogBreeds();
        log.info("Found {} dog breeds", breeds.size());
        return breeds;
    }
}
