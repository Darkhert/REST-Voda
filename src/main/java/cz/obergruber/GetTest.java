package cz.obergruber;

import io.restassured.RestAssured;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GetTest {

    @BeforeSuite
    void setUp() {
        System.out.println("Before");
    }

    @Test
    void get_req() {
        RestAssured.baseURI = "https://api.github.com";
        RestAssured.basePath = "emojis/a";
        given().when().get().then().log().all().statusCode(404).body("message", equalTo("Not Found"));
    }

    @AfterSuite
    void tearDown() {
        System.out.println("After");
    }
}
