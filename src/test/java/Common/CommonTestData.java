package Common;

public class CommonTestData {
    public static int success_status_code = 200;          // OK
    public static int create_success_status_code = 201;   // Resource created
    public static int bad_request_status_code = 400;      // Invalid request / validation error
    public static int unauthorized_status_code = 401;    // Authentication failure
    public static int forbidden_status_code = 403;       // Permission denied
    public static int not_found_status_code = 404;       // Resource not found
    public static int conflict_status_code = 409;        // Duplicate resource / conflict
    public static int internal_server_error = 500;       // Server error
}