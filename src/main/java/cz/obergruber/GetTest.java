package cz.obergruber;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.testng.annotations.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GetTest {

    public User user;
    public String URL = "https://api.spotify.com";

    @DataProvider
    public Object[][] artists() {
        return new Object[][] {
                { "Ylvis", "2lEOFtf3cCyzomQcMHJGfZ" },
                { "Asonance", "7bAfTFyv7hCYceSg6UqeXP" },
                { "Queen", "1dfeR4HaWDbWqFHLkxsg1d" }
        };
    }

    @BeforeSuite
    void setUp() {
        RestAssured.baseURI = URL;
        RestAssured.defaultParser = Parser.JSON;

        this.user = new User("Jirka");
    }

    @Test(groups = "auth", priority = 0)
    void noToken(){
        RestAssured.basePath = "v1/me";
        given().when().get().then().statusCode(401).body("error.message", equalTo("No token provided"));
    }

    @Test(groups = "auth", priority = 0)
    void invalidToken() {
        RestAssured.basePath = "v1/me";
        given().auth().oauth2(this.user.invalidToken).
                when().get().then().statusCode(401).body("error.message", equalTo("Invalid access token"));
    }

    @Test(groups = "auth", priority = 0)
    void expiredToken() {
        RestAssured.basePath = "v1/me";
        given().auth().oauth2(this.user.expiredToken).
                when().get().then().statusCode(401).body("error.message", equalTo("The access token expired"));
    }

    @Test(groups = "auth", priority = 0)
    void correctToken() {
        RestAssured.basePath = "v1/me";
        Response response = given().auth().oauth2(this.user.accessToken).
                when().get().then().log().all().statusCode(200).body("display_name", equalTo("Jirka")).extract().response();

        user.user_id = response.jsonPath().getString("id");
    }

    @Test(groups = "user", priority = 1)
    void topArtist() {
        RestAssured.basePath = "v1/me/top/artists";
        given().auth().oauth2(this.user.accessToken).
                when().get().then().log().all().statusCode(200).body("items[0].name", equalTo("Linkin Park"));
    }

    @Test(groups = "user", priority = 1)
    void topTrack() {
        RestAssured.basePath = "v1/me/top/tracks";
        given().auth().oauth2(this.user.accessToken).queryParam("limit", 1).queryParam("offset", 0).
                when().get().then().log().all().statusCode(200).body("items[0].name", equalTo("In the End"));
    }

    @Test(groups = "content", priority = 2, dataProvider = "artists")
    void searchArtistId(String artist, String id) {
        RestAssured.basePath = "v1/search";
        Response response = given().auth().oauth2(this.user.accessToken).queryParam("q", artist).queryParam("type", "artist").
                when().get().then().log().all().extract().response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(artist, response.jsonPath().getString("artists.items[0].name"));
        Assertions.assertEquals(id, response.jsonPath().getString("artists.items[0].id"));
    }

    @Test(groups = "content", priority = 2, dataProvider = "artists")
    void artistByID(String artist, String id) {
        RestAssured.basePath = String.format("v1/artists/%s", id);
        given().auth().oauth2(this.user.accessToken).
                when().get().then().log().all().statusCode(200).body("name", equalTo(artist));
    }

    @Test(groups = "manipulation", priority = 1, dependsOnMethods = {"correctToken"})
    void createPlaylist() {
        String endpoint = String.format("v1/users/%s/playlists", this.user.user_id);

        String name = "REST";
        String description = "New playlist description";
        Boolean publicPlaylist = false;

        JSONObject requestParams = new JSONObject();
        requestParams.put("name", name);
        requestParams.put("description", description);
        requestParams.put("public", publicPlaylist);

        Response response = given().auth().oauth2(this.user.accessToken).request().body(requestParams.toString()).
                post(String.format("%s/%s", this.URL, endpoint)).then().log().all().extract().response();

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(name, response.jsonPath().getString("name"));
        Assertions.assertEquals(description, response.jsonPath().getString("description"));
        Assertions.assertEquals(publicPlaylist.toString(), response.jsonPath().getString("public"));

        this.user.playlist_id = response.jsonPath().getString("id");
    }

    @Test(groups = "manipulation", priority = 1, dependsOnMethods = {"createPlaylist"})
    void addToPlaylist() {
        String endpoint = String.format("v1/playlists/%s/tracks", this.user.playlist_id);

        Response response = given().auth().oauth2(this.user.accessToken).request().queryParam("uris", String.join(",", this.user.songs)).
                post(String.format("%s/%s", this.URL, endpoint)).then().log().all().extract().response();

        Assertions.assertEquals(201, response.statusCode());
    }

    @Test(groups = "manipulation", priority = 1, dependsOnMethods = {"correctToken", "createPlaylist"})
    void updatePlaylist() {
        String endpoint = String.format("v1/playlists/%s", this.user.playlist_id);

        String name = "REST updated";
        String description = "Updated playlist description";
        Boolean publicPlaylist = true;

        JSONObject requestParams = new JSONObject();
        requestParams.put("name", name);
        requestParams.put("description", description);
        requestParams.put("public", publicPlaylist);

        Response response = given().auth().oauth2(this.user.accessToken).request().body(requestParams.toString()).
                put(String.format("%s/%s", this.URL, endpoint)).then().extract().response();


        String complete = response.asString();
        JsonPath js = new JsonPath(complete);
        System.out.println(js.get(name).toString());
        System.out.println();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(name, response.jsonPath().getString("name"));
        //Assertions.assertEquals(description, response.jsonPath().getString("description"));
        //Assertions.assertEquals(publicPlaylist, response.jsonPath().getBoolean("public"));

        //this.user.playlist_id = response.jsonPath().getString("id");
    }

    @Test(groups = "manipulation", priority = 1, dependsOnMethods = {"addToPlaylist"})
    void removeFromPlaylist() {
        String endpoint = String.format("v1/playlists/%s/tracks", this.user.playlist_id);

        String jsonString = new JSONObject()
                .put("tracks", new JSONArray().
                        put(new JSONObject().
                                put("uri", this.user.songs[0]).
                                put("position", new JSONArray().put(0))))
                .toString();

        Response response = given().auth().oauth2(this.user.accessToken).request().body(jsonString).
                delete(String.format("%s/%s", this.URL, endpoint)).then().log().all().extract().response();

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test(groups = "manipulation", priority = 1, dependsOnMethods = {"removeFromPlaylist"})
    void deletePlaylist() {
        String endpoint = String.format("v1/playlists/%s/followers", this.user.playlist_id);

        Response response = given().auth().oauth2(this.user.accessToken).request().
                delete(String.format("%s/%s", this.URL, endpoint)).then().log().all().extract().response();

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test(groups = "manipulation", priority = 1, dependsOnMethods = {"correctToken"}, dataProvider = "artists")
    void follow(String artist, String id) {
        String endpoint = "v1/me/following";

        Response response = given().auth().oauth2(this.user.accessToken).request().queryParam("type", "artist").queryParam("ids", id).
                put(String.format("%s/%s", this.URL, endpoint)).then().log().all().extract().response();

        Assertions.assertEquals(204, response.statusCode());
    }

    @Test(groups = "manipulation", priority = 1, dependsOnMethods = {"correctToken", "follow"}, dataProvider = "artists")
    void unfollow(String artist, String id) {
        String endpoint = "v1/me/following";

        Response response = given().auth().oauth2(this.user.accessToken).request().queryParam("type", "artist").queryParam("ids", id).
                delete(String.format("%s/%s", this.URL, endpoint)).then().log().all().extract().response();

        Assertions.assertEquals(204, response.statusCode());
    }

    @AfterSuite
    void tearDown() {
        //System.out.println("After");
    }
}
