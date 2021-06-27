package cz.obergruber;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class User extends GetTest {
    public User() {
        super.setUp();
    }

    @Test(groups = "user", priority = 1)
    void topArtist() {
        RestAssured.basePath = "v1/me/top/artists";

        Response response =
        given().auth().oauth2(this.account.accessToken).
        when().get().
        then().extract().response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("Linkin Park", response.jsonPath().getString("items[0].name"));
    }

    @Test(groups = "user", priority = 1)
    void topTrack() {
        RestAssured.basePath = "v1/me/top/tracks";

        Response response =
        given().auth().oauth2(this.account.accessToken).queryParam("limit", 1).queryParam("offset", 0).
        when().get().
        then().extract().response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("In the End", response.jsonPath().getString("items[0].name"));
    }
}
