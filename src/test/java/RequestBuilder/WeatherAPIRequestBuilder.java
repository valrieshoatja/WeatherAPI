package RequestBuilder;

import PayloadBuilder.WeatherAPIPayloadBuilder;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class WeatherAPIRequestBuilder {

    public static String API_KEY = "e6629e336583f54440feeb4b02c5c385";
    public static String WeatherBaseURl = "http://api.openweathermap.org/data/3.0/stations";
    public static String lastStationId;

    public static Response RegisterStation(
            String externalId,
            String name,
            double latitude,
            double longitude,
            int altitude) {

        Response response = given()
                .baseUri(WeatherBaseURl)
                .contentType("application/json")
                .queryParam("appid", API_KEY)
                .body(WeatherAPIPayloadBuilder.updateStationPayload(
                        externalId, name, latitude, longitude, altitude
                ).toString()) // <-- IMPORTANT: Convert JSONObject to String
                .log().all()
                .post("") // baseUri already has /stations
                .then()
                .log().all()
                .extract().response();

        lastStationId = response.jsonPath().getString("id");
        System.out.println("Last Station ID: " + lastStationId);

        return response;
    }
    // negative data
    // Missing external_id
    public static Response RegisterStation_MissingExternalId(String name, double latitude, double longitude, int altitude) {
        Response response = given()
                .baseUri(WeatherBaseURl)
                .queryParam("appid", API_KEY)
                .contentType("application/json")
                .body(WeatherAPIPayloadBuilder.missingExternalIdPayload(name, latitude, longitude, altitude))
                .log().all()
                .post()
                .then().log().all().extract().response();
        return response;
    }
    // Missing station name
    public static Response RegisterStation_MissingName(String externalId, double latitude, double longitude, int altitude) {
        Response response = given()
                .baseUri(WeatherBaseURl)
                .queryParam("appid", API_KEY)
                .contentType("application/json")
                .body(WeatherAPIPayloadBuilder.missingNamePayload(externalId, latitude, longitude, altitude))
                .log().all()
                .post()
                .then().log().all().extract().response();
        return response;
    }
    // Invalid latitude (string instead of number)
    public static Response RegisterStation_InvalidLatitude(String externalId, String name, String invalidLatitude, double longitude, int altitude) {
        Response response = given()
                .baseUri(WeatherBaseURl)
                .queryParam("appid", API_KEY)
                .contentType("application/json")
                .body(WeatherAPIPayloadBuilder.invalidLatitudePayload(externalId, name, invalidLatitude, longitude, altitude))
                .log().all()
                .post()
                .then().log().all().extract().response();
        return response;
    }

    // Invalid longitude (out of range)
    public static Response RegisterStation_InvalidLongitude(String externalId, String name, double latitude, double invalidLongitude, int altitude) {
        Response response = given()
                .baseUri(WeatherBaseURl)
                .queryParam("appid", API_KEY)
                .contentType("application/json")
                .body(WeatherAPIPayloadBuilder.invalidLongitudePayload(externalId, name, latitude, invalidLongitude, altitude))
                .log().all()
                .post()
                .then().log().all().extract().response();
        return response;
    }

    }



