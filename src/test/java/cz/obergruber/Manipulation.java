package cz.obergruber;

import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class Manipulation extends SpotifySuper {
    public Manipulation() {
        super.setUp();
    }

    @Test(groups = "manipulation", priority = 0)
    void createPlaylist() {
        String endpoint = String.format("v1/users/%s/playlists", super.account.user_id);

        String name = "REST";
        String description = "New playlist description";
        Boolean publicPlaylist = false;

        JSONObject requestParams = new JSONObject();
        requestParams.put("name", name);
        requestParams.put("description", description);
        requestParams.put("public", publicPlaylist);

        Response response =
        given().auth().oauth2(super.account.accessToken).request().body(requestParams.toString()).
        when().post(String.format("%s/%s", super.URL, endpoint)).
        then().extract().response();

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(name, response.jsonPath().getString("name"));
        Assertions.assertEquals(description, response.jsonPath().getString("description"));
        Assertions.assertEquals(publicPlaylist.toString(), response.jsonPath().getString("public"));

        super.account.playlist_id = response.jsonPath().getString("id");
    }

    @Test(groups = "manipulation", priority = 1, dependsOnMethods = {"createPlaylist"})
    void addToPlaylist() {
        String endpoint = String.format("v1/playlists/%s/tracks", super.account.playlist_id);

        Response response =
        given().auth().oauth2(super.account.accessToken).request().queryParam("uris", String.join(",", super.account.songs)).
        when().post(String.format("%s/%s", super.URL, endpoint)).
        then().extract().response();

        Assertions.assertEquals(201, response.statusCode());
    }

    @Test(groups = "manipulation", priority = 0, dependsOnMethods = {"createPlaylist"})
    void updatePlaylist() {
        String endpoint = String.format("v1/playlists/%s", super.account.playlist_id);

        JSONObject requestParams = new JSONObject();
        requestParams.put("name", "REST updated");
        requestParams.put("description", "Updated playlist description");
        requestParams.put("public", true);

        Response response =
        given().auth().oauth2(super.account.accessToken).request().body(requestParams.toString()).
        when().put(String.format("%s/%s", super.URL, endpoint)).
        then().extract().response();

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test(groups = "manipulation", priority = 1, dependsOnMethods = {"createPlaylist", "addToPlaylist"})
    void removeFromPlaylist() {
        String endpoint = String.format("v1/playlists/%s/tracks", super.account.playlist_id);

        String jsonString = new JSONObject()
                .put("tracks", new JSONArray().
                        put(new JSONObject().
                                put("uri", super.account.songs[0]).
                                put("position", new JSONArray().put(0))))
                .toString();

        Response response =
        given().auth().oauth2(super.account.accessToken).request().body(jsonString).
        when().delete(String.format("%s/%s", super.URL, endpoint)).
        then().extract().response();

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test(groups = "manipulation", priority = 1, dependsOnMethods = {"createPlaylist"})
    void deletePlaylist() {
        String endpoint = String.format("v1/playlists/%s/followers", super.account.playlist_id);

        Response response =
        given().auth().oauth2(super.account.accessToken).request().
        when().delete(String.format("%s/%s", super.URL, endpoint)).
        then().extract().response();

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test(groups = "manipulation", priority = 1, dataProvider = "artists")
    void follow(String artist, String id) {
        String endpoint = "v1/me/following";

        Response response =
        given().auth().oauth2(super.account.accessToken).request().queryParam("type", "artist").queryParam("ids", id).
        when().put(String.format("%s/%s", super.URL, endpoint)).
        then().extract().response();

        Assertions.assertEquals(204, response.statusCode());
    }

    @Test(groups = "manipulation", priority = 1, dataProvider = "artists")
    void unfollow(String artist, String id) {
        String endpoint = "v1/me/following";

        Response response =
        given().auth().oauth2(super.account.accessToken).request().queryParam("type", "artist").queryParam("ids", id).
        when().delete(String.format("%s/%s", super.URL, endpoint)).
        then().extract().response();

        Assertions.assertEquals(204, response.statusCode());
    }
}
