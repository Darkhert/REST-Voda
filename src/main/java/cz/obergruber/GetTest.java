package cz.obergruber;

import com.tngtech.java.junit.dataprovider.*;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.json.*;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GetTest {

    /*
    https://developer.spotify.com/console/
     */

    String correctToken = "BQD0RjcZv0psziMD1y8VDOiAeXGZ1OiglERQR79Wr-M9ShijKstryWuW65tHHo4uzFi2AoxjI2x1Q6w9LaNittQpq9HngJNphC3kv0ka0lrKJChh39udFRybaIcBGZ4XlsIU6vWXZ8Bn6zoPRjt89LAx-_nK3l0eyFKOpWyeTLc2ubRrAyaBM1tzkjN6qVePCqSqMsWmlnskSZlvTSUpZSjSkPk0Y6L0jtU_9RqcXvQHTllEbtPhRBoCbDMKk4OJPrJZh-pCiBSw5opuUpoTGJqsvA";
    String expiredToken = "BQAhYuk2l9L8d5LYzQuTp0wz7TL29JDZvmsIFoNLbFPWfX4wZ0L4zAAP0YPj4S_RM1fsMdcOPVOlQxEdH-dLrOA-QKyzQl7gqcZ81WMKLIiJRfD6kKVRK1faT2njiFB37i9GlDKqUqVQufFZKE7USvaIon5YsNc0tlGFJqUqydZx5jOP-TSs_K1ehMjgG8e_TRb544-r3im1KLN77bcHY227p-cE86RosQA2xzHf_3yrf1OEh9fg6VqWldr27OqGdGKBZtCpdqAKlFTBosPn8RvIrA";
    String invalidToken = "SK0TwsnCM04csZh02gprc9M4rWeGsBzb4EnWVJ9hciy9F9ADZ60CgpUQejnSZuhHc1LjIQO6Xnnav0rkWEjbJwulaH8g66VWEvFaXIr5MmLZxg1izKou38yJnLeqXWNbkZQM3zNcunFcEi4MDY9a9yiLqkMlHtOahrzvwinz3TONVXyfMsERosAH6BXC9wmXnypFxor6uykufLjhOO7YgXarvG9TvPGSmIp7XtwLHiWkghVU49x9NILlLbaE2nNoVIfbWGt5YZ1XHb3k42TZVV1AM2";
    String user_id = "11157091922";
    String[] songs = new String[] {"spotify:track:4iV5W9uYEdYUVa79Axb7Rh", "spotify:track:1301WleyT98MSxVHPZCA6M"};
    String playlist_id = "";
    String URL = "https://api.spotify.com";
    Object[][] artists = new Object[][] {
        { "Ylvis", "2lEOFtf3cCyzomQcMHJGfZ" },
        { "Asonance", "7bAfTFyv7hCYceSg6UqeXP" },
        { "Queen", "1dfeR4HaWDbWqFHLkxsg1d" }
    };

    @DataProvider
    public Object[][] artists() {
        return artists;
    }

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

    @Test(groups = "user", priority = 1)
    void topArtist() {
        RestAssured.basePath = "v1/me/top/artists";
        given().auth().oauth2(correctToken).
                when().get().then().log().all().statusCode(200).body("items[0].name", equalTo("Powerwolf"));
    }

    @Test(groups = "user", priority = 1)
    void topTrack() {
        RestAssured.basePath = "v1/me/top/tracks";
        given().auth().oauth2(correctToken).queryParam("limit", 1).queryParam("offset", 0).
                when().get().then().log().all().statusCode(200).body("items[0].name", equalTo("Dreamer"));
    }

    @Test(groups = "content", priority = 2, dataProvider = "artists")
    void searchArtistId(String artist, String id) {
        RestAssured.basePath = "v1/search";
        Response response = given().auth().oauth2(correctToken).queryParam("q", artist).queryParam("type", "artist").
                when().get().then().log().all().extract().response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(artist, response.jsonPath().getString("artists.items[0].name"));
        Assertions.assertEquals(id, response.jsonPath().getString("artists.items[0].id"));
    }

    @Test(groups = "content", priority = 2, dataProvider = "artists")
    void artistByID(String artist, String id) {
        RestAssured.basePath = String.format("v1/artists/%s", id);
        given().auth().oauth2(correctToken).
                when().get().then().log().all().statusCode(200).body("name", equalTo(artist));
    }

    @Test(groups = "manipulation", priority = 1, dependsOnMethods = {"correctToken"})
    void createPlaylist() {
        String endpoint = String.format("v1/users/%s/playlists", user_id);

        String name = "REST";
        String description = "New playlist description";
        String publicPlaylist = "false";

        JSONObject requestParams = new JSONObject();
        requestParams.put("name", name);
        requestParams.put("description", description);
        requestParams.put("public", publicPlaylist);

        Response response = given().auth().oauth2(correctToken).request().body(requestParams.toString()).
                post(String.format("%s/%s", URL, endpoint)).then().log().all().extract().response();

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(name, response.jsonPath().getString("name"));
        Assertions.assertEquals(description, response.jsonPath().getString("description"));
        Assertions.assertEquals(publicPlaylist, response.jsonPath().getString("public"));

        playlist_id = response.jsonPath().getString("id");
    }

    @Test(groups = "manipulation", priority = 1, dependsOnMethods = {"createPlaylist"})
    void addToPlaylist() {
        String endpoint = String.format("v1/playlists/%s/tracks", playlist_id);

        Response response = given().auth().oauth2(correctToken).request().queryParam("uris", String.join(",", songs)).
                post(String.format("%s/%s", URL, endpoint)).then().log().all().extract().response();

        Assertions.assertEquals(201, response.statusCode());
    }

    @Test(groups = "manipulation", priority = 1, dependsOnMethods = {"addToPlaylist"})
    void removeFromPlaylist() {
        String endpoint = String.format("v1/playlists/%s/tracks", playlist_id);

        String jsonString = new JSONObject()
                .put("tracks", new JSONArray().
                        put(new JSONObject().
                                put("uri", songs[0]).
                                put("position", new JSONArray().put(0))))
                .toString();

        Response response = given().auth().oauth2(correctToken).request().body(jsonString).
                delete(String.format("%s/%s", URL, endpoint)).then().log().all().extract().response();

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test(groups = "manipulation", priority = 1, dependsOnMethods = {"removeFromPlaylist"})
    void deletePlaylist() {
        String endpoint = String.format("v1/playlists/%s/followers", playlist_id);

        Response response = given().auth().oauth2(correctToken).request().
                delete(String.format("%s/%s", URL, endpoint)).then().log().all().extract().response();

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test(groups = "manipulation", priority = 1, dependsOnMethods = {"correctToken"})
    @Parameters("expectedValue")
    void following(int expectedValue) {
        String endpoint = "v1/me/following";
        System.out.println(expectedValue);
        System.out.println();
        Response response = given().auth().oauth2(correctToken).request().
                delete(String.format("%s/%s", URL, endpoint)).then().log().all().extract().response();

        Assertions.assertEquals(200, response.statusCode());
    }

    @AfterSuite
    void tearDown() {
        //System.out.println("After");
    }
}
