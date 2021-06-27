package cz.obergruber;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class Content extends GetTest {
    public Content() {
        super.setUp();
    }

    @Test(groups = "content", priority = 2, dataProvider = "artists")
    void searchArtistId(String artist, String id) {
        RestAssured.basePath = "v1/search";

        Response response =
        given().auth().oauth2(super.account.accessToken).queryParam("q", artist).queryParam("type", "artist").
        when().get().
        then().extract().response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(artist, response.jsonPath().getString("artists.items[0].name"));
        Assertions.assertEquals(id, response.jsonPath().getString("artists.items[0].id"));
    }

    @Test(groups = "content", priority = 2, dataProvider = "artists")
    void artistByID(String artist, String id) {
        RestAssured.basePath = String.format("v1/artists/%s", id);

        Response response =
        given().auth().oauth2(super.account.accessToken).
        when().get().
        then().extract().response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(artist, response.jsonPath().getString("name"));
    }
}
