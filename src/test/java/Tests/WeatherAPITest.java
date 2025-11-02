package Tests;

import RequestBuilder.WeatherAPIRequestBuilder;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WeatherAPITest {


    //  Store station ID for chaining

    static String stationId;


    // POSITIVE TEST
    @Test(priority = 1)
    public void testRegisterStation_Positive() {
        String uniqueExternalId = "test_station_" + System.currentTimeMillis();

        Response response = WeatherAPIRequestBuilder.RegisterStation(
                uniqueExternalId,      // external_id
                "San Francisco",       // name
                37.76,                 // latitude
                -122.43,               // longitude
                150                    // altitude
        );

        System.out.println("\n=== POSITIVE TEST RESPONSE ===");
        response.then().log().all();

        // Verify HTTP 201 Created
        Assert.assertEquals(response.getStatusCode(), 201, "Expected 201 Created");

        // Extract station ID for chaining
        stationId = response.jsonPath().getString("id");
        System.out.println("Created Station ID: " + stationId);
    }

    //  NEGATIVE TEST: Missing External ID
    @Test(priority = 2)
    public void testRegisterStation_MissingExternalId() {
        Response response = WeatherAPIRequestBuilder.RegisterStation_MissingExternalId(
                "No External ID", 37.76, -122.43, 150);

        System.out.println("\n=== Missing External ID ===");
        response.then().log().all();

        int statusCode = response.getStatusCode();
        System.out.println("Returned status code: " + statusCode);

        // Some APIs return 400, others 422 or 201 (if they ignore missing field)
        Assert.assertTrue(
                statusCode == 400 || statusCode == 422,
                "Expected 400 or 422 for missing external_id, but got: " + statusCode
        );

        // Print response body for analysis
        System.out.println("Response Body: " + response.asString());
    }
    // Missing  station name
    @Test(priority = 2)
    public void testRegisterStation_MissingName() {
        Response response = WeatherAPIRequestBuilder.RegisterStation_MissingName(
                "MISSING_NAME_001", 37.76, -122.43, 150);
        Assert.assertEquals(response.getStatusCode(), 400, "Expected 400 for missing name");
        System.out.println(" Missing name Response: " + response.asString());
    }
    //  Invalid latitude (string instead of number)
    @Test(priority = 3)
    public void testRegisterStation_InvalidLatitude() {
        Response response = WeatherAPIRequestBuilder.RegisterStation_InvalidLatitude(
                "INVALID_LAT_001", "Invalid Latitude", "invalid_latitude", -122.43, 150);
        Assert.assertEquals(response.getStatusCode(), 400, "Expected 400 for invalid latitude type");
        System.out.println(" Invalid latitude Response: " + response.asString());
    }
    //Invalid longitude (out of valid range)
    @Test(priority = 4)
    public void testRegisterStation_InvalidLongitude() {
        Response response = WeatherAPIRequestBuilder.RegisterStation_InvalidLongitude(
                "INVALID_LONG_001", "Invalid Longitude", 37.76, 200.00, 150);
        Assert.assertEquals(response.getStatusCode(), 400, "Expected 400 for invalid longitude");
        System.out.println(" Invalid longitude Response: " + response.asString());
    }
    //  Negative altitude
    @Test(priority = 5)
    public void testRegisterStation_NegativeAltitude() {
        Response response = WeatherAPIRequestBuilder.RegisterStation_NegativeAltitude(
                "NEG_ALT_001", "Negative Altitude", 37.76, -122.43, -50);
        Assert.assertEquals(response.getStatusCode(), 400, "Expected 400 for negative altitude");
        System.out.println(" Negative altitude Response: " + response.asString());
    }
}
