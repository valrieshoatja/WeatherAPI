package PayloadBuilder;

import org.json.JSONObject;

public class WeatherAPIPayloadBuilder {

    // POSITIVE PAYLOAD:
    public static JSONObject registerStationPayload(
            String externalId,
            String name,
            double latitude,
            double longitude,
            int altitude) {

        JSONObject jsonObject = new JSONObject();

        // Add all fields dynamically
        jsonObject.put("external_id", externalId);
        jsonObject.put("name", name);
        jsonObject.put("latitude", latitude);
        jsonObject.put("longitude", longitude);
        jsonObject.put("altitude", altitude);

        // Print the payload for logging
        System.out.println(" Positive Payload: " + jsonObject.toString());

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
        jsonObject.put("name", name);
        jsonObject.put("latitude", latitude);
        jsonObject.put("longitude", longitude);
        jsonObject.put("altitude", altitude);

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
        jsonObject.put("external_id", externalId);
        jsonObject.put("latitude", latitude);
        jsonObject.put("longitude", longitude);
        jsonObject.put("altitude", altitude);

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
        jsonObject.put("latitude", invalidLatitude);
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
        jsonObject.put("altitude", negativeAltitude);

        System.out.println("Negative altitude Payload: " + jsonObject.toString());

        return jsonObject;
    }

    // Empty payload
    public static JSONObject emptyPayload() {
        JSONObject jsonObject = new JSONObject();
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
        jsonObject.put("external_id", existingExternalId);
        jsonObject.put("name", name);
        jsonObject.put("latitude", latitude);
        jsonObject.put("longitude", longitude);
        jsonObject.put("altitude", altitude);

        System.out.println("Duplicate external_id Payload: " + jsonObject.toString());

        return jsonObject;
    }

    // Positive payload for updating a station
    public static JSONObject createStationPayload(String externalId, String name, String latitude, String longitude, String altitude) {
        JSONObject json = new JSONObject();
        json.put("external_id", externalId); // Unique station ID
        json.put("name", name);
        json.put("latitude", latitude);
        json.put("longitude", longitude);
        json.put("altitude", altitude); // A
        return json;
    }

    // Positive Update Payload
    public static JSONObject updateStationPayload(
            String externalId,
            String name,
            double latitude,
            double longitude,
            int altitude) {

        JSONObject json = new JSONObject();
        json.put("external_id", externalId);
        json.put("name", name);
        json.put("latitude", latitude);
        json.put("longitude", longitude);
        json.put("altitude", altitude);

        System.out.println("Update Station Payload (Positive): " + json.toString());
        return json;
    }

    // Missing name (negative)
    public static JSONObject updateStationMissingNamePayload(String externalId, double latitude, double longitude, int altitude) {
        JSONObject json = new JSONObject();
        json.put("external_id", externalId);
        json.put("latitude", latitude);
        json.put("longitude", longitude);
        json.put("altitude", altitude);
        System.out.println("Update Missing Name Payload: " + json.toString());
        return json;
    }

    //  Invalid latitude (Negative)
    public static JSONObject updateStationInvalidLatitudePayload(
            String externalId,
            String name,
            String invalidLatitude,
            double longitude,
            int altitude) {

        JSONObject json = new JSONObject();
        json.put("external_id", externalId);
        json.put("name", name);
        json.put("latitude", invalidLatitude);
        json.put("longitude", longitude);
        json.put("altitude", altitude);

        System.out.println("Update Station Payload (Invalid Latitude): " + json.toString());
        return json;
    }
}