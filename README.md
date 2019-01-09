# Accessing Dog Image and store it in S3

Change the following configurations before running
* AWS credentials
* S3 bucket
* MySQL details

To run
```
mvn spring-boot:run
```
or package the Spring Boot app and run it
```
mvn package
java -jar target/dogbreeder-1.0.0.jar
```

# Consume API

To breed
```
$ curl -X POST http://localhost:8080/dog/breed
```
To get dog
```
$ curl -X GET http://localhost:8080/dog/{dogId}
```
To remove dog
```
$ curl -X DELETE http://localhost:8080/dog/{dogId}
```
To get breed list
```
$ curl -X GET http://localhost:8080/dog/breeds
```
To get dogs by breed
```
$ curl -X GET http://localhost:8080/dog/breed/{breed_name}
```

# S3 Issue

If encounter issue during upload, please check:
* IAM User used for access token have access to the S3 bucket
* May want to change the S3 setting of __Manage public access control lists (ACLs) for this bucket__


# MySQL / MariaDB
If MySQL installation is not preferred, run a docker container of MySQL or MariaDB
e.g.
```
$ docker rm mariadb; docker run --name mariadb -p 3306:3306 -e MYSQL_ROOT_PASSWORD=secret mariadb
```
Link: [MariaDB Docker Official Images](https://hub.docker.com/_/mariadb)
