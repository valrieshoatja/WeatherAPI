package Basics;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class RegisterStationTest {

    // Base URL for OpenWeather Map
    private static final String BASE_URL = "https://api.openweathermap.org/data/3.0/stations";

    // Your personal API key
    private static final String API_KEY = "e6629e336583f54440feeb4b02c5c385";

    @Test
    public void testRegisterStationWithValidData() {
        // Create dynamic station name to avoid duplication
        String stationName = "SF_TestStation_" + UUID.randomUUID().toString().substring(0, 4);

        // Create request body for valid data
        String requestBody = "{\n" +
                "  \"external_id\": \"SF_TEST001\",\n" +
                "  \"name\": \"" + stationName + "\",\n" +
                "  \"latitude\": 37.76,\n" +
                "  \"longitude\": -122.43,\n" +
                "  \"altitude\": 150\n" +
                "}";

        // Print payload
        System.out.println("Positive Payload: " + requestBody);

        // Perform POST request
        Response response = given()
                .baseUri(BASE_URL)
                .queryParam("appid", API_KEY) // API key for authentication
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(201) // Expect 201 Created
                .log().all()
                .extract().response();

        // Assert response status
        Assert.assertEquals(response.getStatusCode(), 201, "Expected status code 201");
    }

    @Test
    public void testRegisterStationWithMissingExternalId() {
        // Missing external_id field
        String requestBody = "{\n" +
                "  \"name\": \"Station_NoID\",\n" +
                "  \"latitude\": 37.76,\n" +
                "  \"longitude\": -122.43,\n" +
                "  \"altitude\": 150\n" +
                "}";

        System.out.println("Missing external_id Payload: " + requestBody);

        Response response = given()
                .baseUri(BASE_URL)
                .queryParam("appid", API_KEY)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(anyOf(is(400), is(422))) // Expect 400 or 422
                .log().all()
                .extract().response();

        Assert.assertTrue(response.getStatusCode() == 400 || response.getStatusCode() == 422,
                "Expected validation error for missing external_id");
    }

    @Test
    public void testRegisterStationWithMissingName() {
        // Missing name field
        String requestBody = "{\n" +
                "  \"external_id\": \"SF_MISSINGNAME\",\n" +
                "  \"latitude\": 37.76,\n" +
                "  \"longitude\": -122.43,\n" +
                "  \"altitude\": 150\n" +
                "}";

        System.out.println("Missing name Payload: " + requestBody);

        Response response = given()
                .baseUri(BASE_URL)
                .queryParam("appid", API_KEY)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(anyOf(is(400), is(422)))
                .log().all()
                .extract().response();

        Assert.assertTrue(response.getStatusCode() == 400 || response.getStatusCode() == 422,
                "Expected validation error for missing name");
    }

    @Test
    public void testRegisterStationWithInvalidLatitude() {
        // Invalid latitude (string instead of number)
        String requestBody = "{\n" +
                "  \"external_id\": \"SF_INVALID_LAT\",\n" +
                "  \"name\": \"InvalidLatitude\",\n" +
                "  \"latitude\": \"NotANumber\",\n" +
                "  \"longitude\": -122.43,\n" +
                "  \"altitude\": 150\n" +
                "}";

        System.out.println("Invalid latitude Payload: " + requestBody);

        Response response = given()
                .baseUri(BASE_URL)
                .queryParam("appid", API_KEY)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(anyOf(is(400), is(422)))
                .log().all()
                .extract().response();

        Assert.assertTrue(response.getStatusCode() == 400 || response.getStatusCode() == 422,
                "Expected validation error for invalid latitude type");
    }

    @Test
    public void testRegisterStationWithInvalidLongitudeRange() {
        // Invalid longitude (out of range)
        String requestBody = "{\n" +
                "  \"external_id\": \"SF_INVALID_LON\",\n" +
                "  \"name\": \"InvalidLongitude\",\n" +
                "  \"latitude\": 37.76,\n" +
                "  \"longitude\": 190,\n" +
                "  \"altitude\": 150\n" +
                "}";

        System.out.println("Invalid longitude Payload: " + requestBody);

        Response response = given()
                .baseUri(BASE_URL)
                .queryParam("appid", API_KEY)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(anyOf(is(400), is(422)))
                .log().all()
                .extract().response();

        Assert.assertTrue(response.getStatusCode() == 400 || response.getStatusCode() == 422,
                "Expected validation error for longitude out of range");
    }
}