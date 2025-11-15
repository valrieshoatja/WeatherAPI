package RequestBuilder;

import PayloadBuilder.WeatherAPIPayloadBuilder;
import io.restassured.response.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;


public class WeatherAPIRequestBuilder {

    // Find API key from env, system property, or local.properties
    private static String findApiKey() {

        // 1. Environment variable
        String key = System.getenv("OPENWEATHER_API_KEY");
        if (isValid(key)) return key;

        // 2. JVM system property (-Dopenweather.api.key=XYZ)
        key = System.getProperty("openweather.api.key");
        if (isValid(key)) return key;

        // 3. local.properties file
        try {
            File propFile = new File(System.getProperty("user.dir"), "local.properties");
            if (propFile.exists()) {
                Properties props = new Properties();
                props.load(new FileInputStream(propFile));
                key = props.getProperty("openweather.api.key");
                if (isValid(key)) return key;
            }
        } catch (Exception ignored) {}

        return null; // none found
    }

    // Small helper to avoid repeating checks
    private static boolean isValid(String key) {
        return key != null && !key.isBlank();
    }

    // Throwing getter kept for existing callers
    private static String getApiKey() {
        String key = findApiKey();
        if (key == null) {
            throw new IllegalStateException("OpenWeather API key not set. Set env var OPENWEATHER_API_KEY, system property -Dopenweather.api.key=<key>, or add 'openweather.api.key=...' to local.properties in project root.");
        }
        return key;
    }

    // Public non-throwing helper for tests/other callers to check presence
    public static String getApiKeyIfPresent() {
        return findApiKey();
    }

    // Base URL should be host+version; endpoints appended per-request
    public static String WeatherBaseURL = "https://api.openweathermap.org/data/3.0";
    public static String lastStationId;

    // register station with positive data
    public static Response RegisterStation(
            String externalId,
            String name,
            double latitude,
            double longitude,
            int altitude) {

        Response response = given()
                .baseUri(WeatherBaseURL)
                .contentType("application/json")
                .queryParam("appid", getApiKey())
                .body(WeatherAPIPayloadBuilder.registerStationPayload(
                        externalId, name, latitude, longitude, altitude
                ).toString())
                .log().all()
                .post("/stations")
                .then()
                .log().all()
                .extract().response();

        // store id if present
        try {
            lastStationId = response.jsonPath().getString("id");
            System.out.println("Registered station ID: " + lastStationId);
        } catch (Exception e) {
            lastStationId = null;
        }

        return response;
    }

    // negative data
    // Missing external_id
    public static Response RegisterStation_MissingExternalId(String name, double latitude, double longitude, int altitude) {
        Response response = given()
                .baseUri(WeatherBaseURL)
                .queryParam("appid", getApiKey())
                .contentType("application/json")
                .body(WeatherAPIPayloadBuilder.missingExternalIdPayload(name, latitude, longitude, altitude).toString())
                .log().all()
                .post("/stations")
                .then().log().all().extract().response();
        return response;
    }

    // Missing station name
    public static Response RegisterStation_MissingName(String externalId, double latitude, double longitude, int altitude) {
        Response response = given()
                .baseUri(WeatherBaseURL)
                .queryParam("appid", getApiKey())
                .contentType("application/json")
                .body(WeatherAPIPayloadBuilder.missingNamePayload(externalId, latitude, longitude, altitude).toString())
                .log().all()
                .post("/stations")
                .then().log().all().extract().response();
        return response;
    }

    // Invalid latitude (string instead of number)
    public static Response RegisterStation_InvalidLatitude(String externalId, String name, String invalidLatitude, double longitude, int altitude) {
        Response response = given()
                .baseUri(WeatherBaseURL)
                .queryParam("appid", getApiKey())
                .contentType("application/json")
                .body(WeatherAPIPayloadBuilder.invalidLatitudePayload(externalId, name, invalidLatitude, longitude, altitude).toString())
                .log().all()
                .post("/stations")
                .then().log().all().extract().response();
        return response;
    }

    // Invalid longitude (out of range)
    public static Response RegisterStation_InvalidLongitude(String externalId, String name, double latitude, double invalidLongitude, int altitude) {
        Response response = given()
                .baseUri(WeatherBaseURL)
                .queryParam("appid", getApiKey())
                .contentType("application/json")
                .body(WeatherAPIPayloadBuilder.invalidLongitudePayload(externalId, name, latitude, invalidLongitude, altitude).toString())
                .log().all()
                .post("/stations")
                .then().log().all().extract().response();
        return response;
    }

    // Negative altitude
    public static Response RegisterStation_NegativeAltitude(String externalId, String name, double latitude, double longitude, int negativeAltitude) {
        Response response = given()
                .baseUri(WeatherBaseURL)
                .queryParam("appid", getApiKey())
                .contentType("application/json")
                .body(WeatherAPIPayloadBuilder.negativeAltitudePayload(externalId, name, latitude, longitude, negativeAltitude).toString())
                .log().all()
                .post("/stations")
                .then().log().all().extract().response();
        return response;
    }

    // Empty payload
    public static Response RegisterStation_EmptyPayload() {
        Response response = given()
                .baseUri(WeatherBaseURL)
                .queryParam("appid", getApiKey())
                .contentType("application/json")
                .body(WeatherAPIPayloadBuilder.emptyPayload().toString())
                .log().all()
                .post("/stations")
                .then().log().all().extract().response();
        return response;
    }

    //  Duplicate external_id
    public static Response RegisterStation_DuplicateExternalId(String existingExternalId, String name, double latitude, double longitude, int altitude) {
        Response response = given()
                .baseUri(WeatherBaseURL)
                .queryParam("appid", getApiKey())
                .contentType("application/json")
                .body(WeatherAPIPayloadBuilder.duplicateExternalIdPayload(existingExternalId, name, latitude, longitude, altitude).toString())
                .log().all()
                .post("/stations")
                .then().log().all().extract().response();
        return response;
    }
    // GET STATION BY ID

    public static Response GetStationById(String stationId) {
        Response response = given()
                .baseUri(WeatherBaseURL)
                .queryParam("appid", getApiKey())
                .log().all()
                .get("/stations/" + stationId)
                .then()
                .log().all()
                .extract().response();

        return response;
    }

    // Get all stations
    public static Response GetAllStations() {
        return given()
                .baseUri(WeatherBaseURL)
                .queryParam("appid", getApiKey())
                .log().all()
                .get("/stations")
                .then()
                .log().all()
                .extract().response();
    }

    // Positive update — update station name or coordinates
    public static Response UpdateStation(String stationId, String externalId, String name, double latitude, double longitude, int altitude) {
        Response response = given()
                .baseUri(WeatherBaseURL)
                .queryParam("appid", getApiKey())
                .contentType("application/json")
                .body(WeatherAPIPayloadBuilder.updateStationPayload(externalId, name, latitude, longitude, altitude).toString())
                .log().all()
                .put("/stations/" + stationId)
                .then().log().all().extract().response();

        return response;
    }

    // Negative: Missing name
    public static Response UpdateStation_MissingName(String stationId, String externalId, double latitude, double longitude, int altitude) {
        Response response = given()
                .baseUri(WeatherBaseURL)
                .queryParam("appid", getApiKey())
                .contentType("application/json")
                .body(WeatherAPIPayloadBuilder.updateStationMissingNamePayload(externalId, latitude, longitude, altitude).toString())
                .log().all()
                .put("/stations/" + stationId)
                .then().log().all().extract().response();

        return response;
    }
    // Negative: Invalid latitude
    public static Response UpdateStation_InvalidLatitude(String stationId, String externalId, String name, String invalidLatitude, double longitude, int altitude) {
        Response response = given()
                .baseUri(WeatherBaseURL)
                .queryParam("appid", getApiKey())
                .contentType("application/json")
                .body(WeatherAPIPayloadBuilder.updateStationInvalidLatitudePayload(externalId, name, invalidLatitude, longitude, altitude).toString())
                .log().all()
                .put("/stations/" + stationId)
                .then().log().all().extract().response();

        return response;
    }
    // Negative: Empty payload
    public static Response UpdateStation_EmptyPayload(String stationId) {
        Response response = given()
                .baseUri(WeatherBaseURL)
                .queryParam("appid", getApiKey())
                .contentType("application/json")
                .body(WeatherAPIPayloadBuilder.emptyPayload().toString())
                .log().all()
                .put("/stations/" + stationId)
                .then().log().all().extract().response();

        return response;
    }

    // Negative: Invalid ID ( null)
    public static Response UpdateStation_InvalidId(String invalidId, String externalId, String name, double latitude, double longitude, int altitude) {
        Response response = given()
                .baseUri(WeatherBaseURL)
                .queryParam("appid", getApiKey())
                .contentType("application/json")
                .body(WeatherAPIPayloadBuilder.updateStationPayload(externalId, name, latitude, longitude, altitude).toString())
                .log().all()
                .put("/stations/" + invalidId)
                .then().log().all().extract().response();

        return response;
    }
    //  Delete existing station by ID
    public static Response DeleteStation(String stationId) {
        System.out.println("Deleting Station ID: " + stationId);
        Response response = given()
                .baseUri(WeatherBaseURL)
                .queryParam("appid", getApiKey())
                .log().all()
                .delete("/stations/" + stationId)
                .then()
                .log().all()
                .extract()
                .response();
        return response;
    }

    //  Delete invalid or non-existing station (Negative Test)
    public static Response DeleteStation_InvalidId(String invalidStationId) {
        System.out.println("Attempting to Delete Invalid Station ID: " + invalidStationId);
        Response response = given()
                .baseUri(WeatherBaseURL)
                .queryParam("appid", getApiKey())
                .log().all()
                .delete("/stations/" + invalidStationId)
                .then()
                .log().all()
                .extract()
                .response();
        return response;
    }

    // Confirm Deletion
    public static Response ConfirmStationDeleted(String stationId) {
        System.out.println("Confirming deletion of Station ID: " + stationId);
        Response response = given()
                .baseUri(WeatherBaseURL)
                .queryParam("appid", getApiKey())
                .log().all()
                .get("/stations/" + stationId)
                .then()
                .log().all()
                .extract()
                .response();
        return response;
    }
}

