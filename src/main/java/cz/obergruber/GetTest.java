package cz.obergruber;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.json.*;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GetTest {

    String correctToken = "BQCcaVVrmKLxB7YgsfvWy8U6yydgTKgrRO4Qvy-_7zqw6lzExVQr6TMCAJ3zHBhOU2-e0UdQH2T3tgxvN4fg7WuKseVoZk7DDMUydX22Lgsa5xEKPCouyS3jzCKgoimzivGvAgsv27UyCseVn5_W7X1RS6jVHG2zv_wGIZJcksumaYd-Z9TOjLG3SkP-uxLWTuM30DRfziDgwmcJoTOYsieeUHPromjowIm19USlwU2yU7-UB_pT7vG1ZlAPklmm-R7af-vqYb29X1k9g7oukNslmw";
    String expiredToken = "BQAhYuk2l9L8d5LYzQuTp0wz7TL29JDZvmsIFoNLbFPWfX4wZ0L4zAAP0YPj4S_RM1fsMdcOPVOlQxEdH-dLrOA-QKyzQl7gqcZ81WMKLIiJRfD6kKVRK1faT2njiFB37i9GlDKqUqVQufFZKE7USvaIon5YsNc0tlGFJqUqydZx5jOP-TSs_K1ehMjgG8e_TRb544-r3im1KLN77bcHY227p-cE86RosQA2xzHf_3yrf1OEh9fg6VqWldr27OqGdGKBZtCpdqAKlFTBosPn8RvIrA";
    String invalidToken = "SK0TwsnCM04csZh02gprc9M4rWeGsBzb4EnWVJ9hciy9F9ADZ60CgpUQejnSZuhHc1LjIQO6Xnnav0rkWEjbJwulaH8g66VWEvFaXIr5MmLZxg1izKou38yJnLeqXWNbkZQM3zNcunFcEi4MDY9a9yiLqkMlHtOahrzvwinz3TONVXyfMsERosAH6BXC9wmXnypFxor6uykufLjhOO7YgXarvG9TvPGSmIp7XtwLHiWkghVU49x9NILlLbaE2nNoVIfbWGt5YZ1XHb3k42TZVV1AM2";
    String user_id = "11157091922";
    String[] songs = new String[] {"spotify:track:4iV5W9uYEdYUVa79Axb7Rh", "spotify:track:1301WleyT98MSxVHPZCA6M"};
    String playlist_id = "";
    String URL = "https://api.spotify.com";

    @BeforeSuite
    void setUp() {
        RestAssured.baseURI = URL;
        RestAssured.defaultParser = Parser.JSON;

        // TODO Add parallel run
    }

    @Test(groups = "auth", priority = 0)
    void noToken(){
        RestAssured.basePath = "v1/me";
        given().when().get().then().statusCode(401).body("error.message", equalTo("No token provided"));
    }

    @Test(groups = "auth", priority = 0)
    void invalidToken() {
        RestAssured.basePath = "v1/me";
        given().auth().oauth2(invalidToken).
                when().get().then().statusCode(401).body("error.message", equalTo("Invalid access token"));
    }

    @Test(groups = "auth", priority = 0)
    void expiredToken() {
        RestAssured.basePath = "v1/me";
        given().auth().oauth2(expiredToken).
                when().get().then().statusCode(401).body("error.message", equalTo("The access token expired"));
    }

    @Test(groups = "auth", priority = 0)
    void correctToken() {
        RestAssured.basePath = "v1/me";
        given().auth().oauth2(correctToken).
                when().get().then().log().all().statusCode(200).body("display_name", equalTo("Michal Obergruber"));
    }


    @AfterSuite
    void tearDown() {
        //System.out.println("After");
    }
}
