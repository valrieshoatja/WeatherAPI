package Tests;

import RequestBuilder.WeatherAPIRequestBuilder;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WeatherAPITest {

    // Store station ID for chaining requests
    static String stationId;

    // ----------------------------
    // POSITIVE TEST: Create station
    // ----------------------------
    @Test(priority = 1)
    public void testRegisterStation_Positive() {
        Response response = WeatherAPIRequestBuilder.RegisterStation(
                "SF_TEST001",       // external_id
                "San Francisco",    // name
                37.76,              // latitude
                -122.43,            // longitude
                150                 // altitude
        );

        // Assert status code 201 Created
        Assert.assertEquals(response.getStatusCode(), 201);

        // Extract station ID for chaining
        stationId = response.jsonPath().getString("id");
        System.out.println("Created Station ID: " + stationId);
    }
}
    
    
    
