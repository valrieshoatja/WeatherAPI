package RequestBuilder;

import PayloadBuilder.WeatherAPIPayloadBuilder;
import io.restassured.response.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;


public class WeatherAPIRequestBuilder {

    // Return key if present in any supported location, otherwise null
    private static String findApiKey() {
        String key = System.getenv("OPENWEATHER_API_KEY");
        if (key != null && !key.isBlank()) return key.trim();

        key = System.getProperty("openweather.api.key");
        if (key != null && !key.isBlank()) return key.trim();

        // Look for local.properties in project root
        String userDir = System.getProperty("user.dir");
        File propFile = new File(userDir, "local.properties");
        if (propFile.exists() && propFile.isFile()) {
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(propFile)) {
                props.load(fis);
                key = props.getProperty("openweather.api.key");
                if (key != null && !key.isBlank()) return key.trim();
            } catch (IOException ignored) {
                // ignore and fall through to return null
            }
        }

        return null;
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


}
