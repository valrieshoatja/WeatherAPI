package Tests;

import RequestBuilder.WeatherAPIRequestBuilder;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class WeatherAPITest {


    //  Store station ID for chaining

    static String stationId; //store id of created station
    static String createdExternalId; // store external id for duplicate test

    @BeforeClass
    public void ensureApiKeyPresent() {
        String key = WeatherAPIRequestBuilder.getApiKeyIfPresent();
        if (key == null || key.isBlank()) {
            throw new SkipException("OpenWeather API key not set. Set environment variable OPENWEATHER_API_KEY, system property -Dopenweather.api.key=<key>, or add 'openweather.api.key=...' to local.properties in project root to run these tests.");
        }
    }

    // POSITIVE TEST
    @Test(priority = 1)
    public void testRegisterStation_Positive() {
        String uniqueExternalId = "test_station_" + System.currentTimeMillis();

        Response response = WeatherAPIRequestBuilder.RegisterStation(
                uniqueExternalId, "San Francisco", 37.76, -122.43, 150);

        System.out.println("=== REGISTER STATION RESPONSE ===");
        response.then().log().all();

        Assert.assertEquals(response.getStatusCode(), 201, "Expected 201 Created");

        // ✅ First try lowercase (because JsonPath converts keys to lowercase)
        String stationId = response.jsonPath().getString("id");
        if (stationId == null || stationId.isEmpty()) {
            stationId = response.jsonPath().getString("ID");
        }

        System.out.println("Extracted station ID: " + stationId);
        Assert.assertNotNull(stationId, "Station ID missing in registration response; see console for full response body");

        WeatherAPITest.stationId = stationId;
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
    @Test(priority = 3)
    public void testRegisterStation_MissingName() {
        Response response = WeatherAPIRequestBuilder.RegisterStation_MissingName(
                "MISSING_NAME_001", 37.76, -122.43, 150);
        Assert.assertEquals(response.getStatusCode(), 400, "Expected 400 for missing name");
        System.out.println(" Missing name Response: " + response.asString());
    }
    //  Invalid latitude (string instead of number)
    @Test(priority = 4)
    public void testRegisterStation_InvalidLatitude() {
        Response response = WeatherAPIRequestBuilder.RegisterStation_InvalidLatitude(
                "INVALID_LAT_001", "Invalid Latitude", "invalid_latitude", -122.43, 150);
        Assert.assertEquals(response.getStatusCode(), 400, "Expected 400 for invalid latitude type");
        System.out.println(" Invalid latitude Response: " + response.asString());
    }
    //Invalid longitude (out of valid range)
    @Test(priority = 5)
    public void testRegisterStation_InvalidLongitude() {
        Response response = WeatherAPIRequestBuilder.RegisterStation_InvalidLongitude(
                "INVALID_LONG_001", "Invalid Longitude", 37.76, 200.00, 150);
        Assert.assertEquals(response.getStatusCode(), 400, "Expected 400 for invalid longitude");
        System.out.println(" Invalid longitude Response: " + response.asString());
    }
    //  Negative altitude
    @Test(priority = 6)
    public void testRegisterStation_NegativeAltitude() {
        System.out.println("=== Negative Altitude Test ===");

        Response response = WeatherAPIRequestBuilder.RegisterStation(
                "NEG_ALT_001", "Negative Altitude", 37.76, -122.43, -50);

        response.then().log().all();

        int actualStatus = response.getStatusCode();

        // OpenWeather accepts negative altitudes → expect 201 Created
        Assert.assertEquals(actualStatus, 201, "Expected success (201) for valid negative altitude");

        // Verify that response still includes station data
        String name = response.jsonPath().getString("name");
        Assert.assertEquals(name, "Negative Altitude", "Station name mismatch");

    }
    //  Empty payload
    @Test(priority = 7)
    public void testRegisterStation_EmptyPayload() {
        Response response = WeatherAPIRequestBuilder.RegisterStation_EmptyPayload();
        Assert.assertEquals(response.getStatusCode(), 400, "Expected 400 for empty payload");
        System.out.println(" Empty payload Response: " + response.asString());
    }
    // Duplicate external_id (uses one created from the positive test)
    @Test(priority = 8, dependsOnMethods = {"testRegisterStation_Positive"})
    public void testRegisterStation_DuplicateExternalId() {
        // Reuse the external_id created in positive test
        Response response = WeatherAPIRequestBuilder.RegisterStation_DuplicateExternalId(
                createdExternalId, "Duplicate Test", 37.76, -122.43, 150);

        System.out.println("\n===  Duplicate External ID ===");
        response.then().log().all();

        int statusCode = response.getStatusCode();

        // Some APIs return 409 Conflict, others may return 400 or 201
        if (statusCode == 409 || statusCode == 400) {
            System.out.println("Duplicate external_id correctly rejected (status " + statusCode + ")");
        } else if (statusCode == 201) {
            System.out.println(" API allowed duplicate external_id — non-standard behavior");
        } else {
            Assert.fail("Unexpected status code for duplicate external_id: " + statusCode);
        }
    }

    @Test(priority = 9, dependsOnMethods = {"testRegisterStation_Positive"})
    public void testGetStationById() {

        // Use stationId set by the positive test
        Assert.assertNotNull(stationId, "No station ID found. Ensure a station is registered before this test.");

        Response response = WeatherAPIRequestBuilder.GetStationById(stationId);
        Assert.assertEquals(response.getStatusCode(), 200, "Expected 200 OK for get station");
        String fetchedId = response.jsonPath().getString("id");
        Assert.assertEquals(fetchedId, stationId, "Fetched station ID should match created ID");
        System.out.println("Fetched Station Details: " + response.asString());
    }
    @Test(priority = 10)
    public void testGetStationByInvalidId() {
        Response response = WeatherAPIRequestBuilder.GetStationById("invalid_station_id");
        Assert.assertEquals(response.getStatusCode(), 400);
    }
    @Test(priority = 11)
    public void testGetStationByNullId() {
        Response response = WeatherAPIRequestBuilder.GetStationById("null"); // pass string "null"
        Assert.assertEquals(response.getStatusCode(), 400, "Expected 400 for null station ID");
        System.out.println("Response for null ID: " + response.getBody().asString());
    }
    @Test(priority = 12, dependsOnMethods = {"testRegisterStation_Positive"})
    public void testGetAllStations() {
        Response response = WeatherAPIRequestBuilder.GetAllStations();
        Assert.assertEquals(response.getStatusCode(), 200);

        boolean stationFound = response.jsonPath().getList("id").contains(stationId);
        Assert.assertTrue(stationFound, "Newly registered station not found in station list");
        System.out.println("Newly registered station is present in all stations list.");
    }
    // Positive test
    @Test(priority = 13, dependsOnMethods = {"testRegisterStation_Positive"})
    public void testUpdateStation_Positive() {
        Assert.assertNotNull(stationId, "Station ID must be set from positive registration test.");

        Response response = WeatherAPIRequestBuilder.UpdateStation(
                stationId, "UPDATED_EXT_" + System.currentTimeMillis(), "Updated Station Name", 40.71, -74.01, 200);

        System.out.println("\n=== UPDATE STATION POSITIVE ===");
        response.then().log().all();

        Assert.assertEquals(response.getStatusCode(), 200, "Expected 200 OK for update");

        String updatedName = response.jsonPath().getString("name");
        Assert.assertEquals(updatedName, "Updated Station Name", "Station name not updated correctly");
    }
    // Missing name (negative)
    @Test(priority = 14)
    public void testUpdateStation_MissingName() {
        Response response = WeatherAPIRequestBuilder.UpdateStation_MissingName(
                stationId, "UPD_MISSNAME_001", 40.71, -74.01, 200);

        Assert.assertTrue(response.getStatusCode() == 400 || response.getStatusCode() == 422,
                "Expected 400 or 422 for missing name, got: " + response.getStatusCode());
        System.out.println("Response Missing Name: " + response.asString());
    }
}