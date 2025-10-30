package PayloadBuilder;

import org.json.JSONObject;

public class WeatherAPIPayloadBuilder {

    //  POSITIVE PAYLOAD

    public static JSONObject updateStationPayload(
            String externalId,
            String name,
            double latitude,
            double longitude,
            int altitude) {

        // Create new JSON object
        JSONObject jsonObject = new JSONObject();

        // Add all fields
        jsonObject.put("external_id", externalId);   // External ID
        jsonObject.put("name", name);                // Station name
        jsonObject.put("latitude", latitude);        // Latitude
        jsonObject.put("longitude", longitude);      // Longitude
        jsonObject.put("altitude", altitude);        // Altitude

        // Print the payload for logging
        System.out.println("Positive Payload: " + jsonObject.toString());

        return jsonObject;
    }

    //NEGATIVE PAYLOADS

    // Missing external_id
    public static JSONObject missingExternalIdPayload(
            String name,
            double latitude,
            double longitude,
            int altitude) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);                // Name only
        jsonObject.put("latitude", latitude);        // Latitude
        jsonObject.put("longitude", longitude);      // Longitude
        jsonObject.put("altitude", altitude);        // Altitude

        // Print payload for debugging
        System.out.println("Missing external_id Payload: " + jsonObject.toString());

        return jsonObject;
    }

    // Missing name
    public static JSONObject missingNamePayload(
            String externalId,
            double latitude,
            double longitude,
            int altitude) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("external_id", externalId);   // External ID only
        jsonObject.put("latitude", latitude);        // Latitude
        jsonObject.put("longitude", longitude);      // Longitude
        jsonObject.put("altitude", altitude);        // Altitude

        System.out.println("Missing name Payload: " + jsonObject.toString());

        return jsonObject;
    }

    // Invalid latitude (string instead of number)
    public static JSONObject invalidLatitudePayload(
            String externalId,
            String name,
            String invalidLatitude, // intentionally wrong type
            double longitude,
            int altitude) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("external_id", externalId);
        jsonObject.put("name", name);
        jsonObject.put("latitude", invalidLatitude); // invalid
        jsonObject.put("longitude", longitude);
        jsonObject.put("altitude", altitude);

        System.out.println("Invalid latitude Payload: " + jsonObject.toString());

        return jsonObject;
    }

    // Invalid longitude (out of valid range)
    public static JSONObject invalidLongitudePayload(
            String externalId,
            String name,
            double latitude,
            double invalidLongitude,
            int altitude) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("external_id", externalId);
        jsonObject.put("name", name);
        jsonObject.put("latitude", latitude);
        jsonObject.put("longitude", invalidLongitude); // intentionally invalid
        jsonObject.put("altitude", altitude);

        System.out.println("Invalid longitude Payload: " + jsonObject.toString());

        return jsonObject;
    }

    // Negative altitude
    public static JSONObject negativeAltitudePayload(
            String externalId,
            String name,
            double latitude,
            double longitude,
            int negativeAltitude) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("external_id", externalId);
        jsonObject.put("name", name);
        jsonObject.put("latitude", latitude);
        jsonObject.put("longitude", longitude);
        jsonObject.put("altitude", negativeAltitude); // negative value

        System.out.println("Negative altitude Payload: " + jsonObject.toString());

        return jsonObject;
    }

    // Empty payload
    public static JSONObject emptyPayload() {
        JSONObject jsonObject = new JSONObject(); // completely empty
        System.out.println("Empty Payload: " + jsonObject.toString());
        return jsonObject;
    }

    // Duplicate external_id
    public static JSONObject duplicateExternalIdPayload(
            String existingExternalId,
            String name,
            double latitude,
            double longitude,
            int altitude) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("external_id", existingExternalId); // duplicate
        jsonObject.put("name", name);
        jsonObject.put("latitude", latitude);
        jsonObject.put("longitude", longitude);
        jsonObject.put("altitude", altitude);

        System.out.println("Duplicate external_id Payload: " + jsonObject.toString());

        return jsonObject;
    }
}
