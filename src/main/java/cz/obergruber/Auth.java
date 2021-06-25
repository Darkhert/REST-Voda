package cz.obergruber;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class Auth extends GetTest {
    public Auth() {
        super.setUp();
    }

    @Test(groups = "auth", priority = 0)
    void noToken() {
        RestAssured.basePath = "v1/me";

        given().
        when().get().
        then().statusCode(401).body("error.message", equalTo("No token provided"));
    }

    @Test(groups = "auth", priority = 0)
    void invalidToken() {
        RestAssured.basePath = "v1/me";

        given().auth().oauth2(super.account.invalidToken).
        when().get().
        then().statusCode(401).body("error.message", equalTo("Invalid access token"));
    }

    @Test(groups = "auth", priority = 0)
    void expiredToken() {
        RestAssured.basePath = "v1/me";

        given().auth().oauth2(super.account.expiredToken).
        when().get().
        then().statusCode(401).body("error.message", equalTo("The access token expired"));
    }

    @Test(groups = "auth", priority = 0)
    void correctToken() {
        RestAssured.basePath = "v1/me";

        Response response =
        given().auth().oauth2(super.account.accessToken).
        when().get().
        then().extract().response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(super.account.name, response.jsonPath().getString("display_name"));
        Assertions.assertEquals(super.account.user_id, response.jsonPath().getString("id"));
    }
}
