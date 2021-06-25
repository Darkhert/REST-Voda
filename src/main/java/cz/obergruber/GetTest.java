package cz.obergruber;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;

public class GetTest {
    public GetTest() {

    }

    public Account account;
    public String URL = "https://api.spotify.com";

    @DataProvider
    public Object[][] artists() {
        return new Object[][]{
                {"Ylvis", "2lEOFtf3cCyzomQcMHJGfZ"},
                {"Asonance", "7bAfTFyv7hCYceSg6UqeXP"},
                {"Queen", "1dfeR4HaWDbWqFHLkxsg1d"}
        };
    }

    void setUp() {
        RestAssured.baseURI = URL;
        RestAssured.defaultParser = Parser.JSON;

        this.account = new Account();
    }

    @BeforeSuite
    void before() {
        System.out.println("Start");
    }

    @AfterSuite
    void tearDown() {
        System.out.println("End");
    }
}
