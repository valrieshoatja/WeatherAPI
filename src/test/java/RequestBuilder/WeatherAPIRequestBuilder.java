package RequestBuilder;

import PayloadBuilder.WeatherAPIPayloadBuilder;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;


public class WeatherAPIRequestBuilder {

    private static String getApiKey() {
        String key = System.getenv("OPENWEATHER_API_KEY");
        if (key == null || key.isBlank()) {
            key = System.getProperty("openweather.api.key");
        }
        if (key == null || key.isBlank()) {
            throw new IllegalStateException("OpenWeather API key not set. Set env var OPENWEATHER_API_KEY or pass -Dopenweather.api.key=<key>");
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


}
