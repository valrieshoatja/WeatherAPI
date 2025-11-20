package RequestBuilder;

import PayloadBuilder.WeatherAPIPayloadBuilder;
import io.restassured.response.Response;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import static io.restassured.RestAssured.given;

import java.io.InputStream;
import java.util.Properties;

public class WeatherAPIRequestBuilder {

    private static final String PROP_NAME = "openweather.api.key";

    private static String findApiKey() {

        // Env variable
        String key = System.getenv("OPENWEATHER_API_KEY");
        if (isValid(key)) return key;

        // System property
        key = System.getProperty(PROP_NAME);
        if (isValid(key)) return key;

        // src/test/resources/config/local.properties
        try (InputStream input = WeatherAPIRequestBuilder.class
                .getClassLoader()
                .getResourceAsStream("config/local.properties")) {

            if (input != null) {
                Properties p = new Properties();
                p.load(input);
                key = p.getProperty(PROP_NAME);
                if (isValid(key)) return key;
            }
        } catch (Exception ignored) {}

        return null;
    }

    private static boolean isValid(String v) {
        return v != null && !v.isBlank();
    }

    public static String getApiKeyIfPresent() {
        return findApiKey();
    }

    private static String getApiKey() {
        String key = findApiKey();
        if (!isValid(key)) {
            throw new IllegalStateException(
                    "API key missing. Add to src/test/resources/config/local.properties as:\n" +
                            "openweather.api.key=YOUR_KEY"
            );
        }
        return key;
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

