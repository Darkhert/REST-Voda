package cz.obergruber;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.net.URI;

import static io.restassured.RestAssured.given;

public class RefreshToken {
    private void refresh() {
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId("1ae3f31371fd4019b858fe7919c42102")
                .setClientSecret("8a58db9e0ae149fc9025df57ebe83cbd")
                .setRedirectUri(SpotifyHttpManager.makeUri("https://example.com/callback"))
                .build();

        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .state("x4xkmn9pu3j6ukrs8n")
                .scope("user-read-playback-position")
                .show_dialog(true).build();

        URI uri = authorizationCodeUriRequest.execute();

        System.out.println("URI: " + uri.toString());
        System.out.println();

        Response response = given().contentType(ContentType.URLENC).when().get(uri).then().log().all().extract().response();
        System.out.println(response.getBody().asString());
        String code = "AQCxi8dS4sj9nl0EMLkOzqkugmUm38dQ-_ha0wc5oRBD4-Q8-JX_Pn0kT_G4gP574EWfkgr4k7Dsf5uqZicyHRcL-ySJeU7zulTlg4zu5Y_aAbuj1RmT1yq7kTRYpkdoK7piI96OllvI0GYfVLSOl0nDnO7YYqqQHKXvu6hBeMuhEoVj02NjAWCKTMG2a8ofLI1NJjuzBwW5UHZCGw";

        System.out.println(response);
        System.out.println();

        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();
        String refreshToken = "";
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
            refreshToken = authorizationCodeCredentials.getRefreshToken();
            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
            System.out.println(authorizationCodeCredentials.getRefreshToken());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }

        spotifyApi = new SpotifyApi.Builder()
                .setClientId("1ae3f31371fd4019b858fe7919c42102")
                .setClientSecret("8a58db9e0ae149fc9025df57ebe83cbd")
                .setRefreshToken(refreshToken)
                .build();

        AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh().build();

        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());

            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
            System.out.println(authorizationCodeCredentials.getAccessToken());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }
}
