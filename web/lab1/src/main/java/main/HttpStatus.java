package main;

enum HttpStatus {
  OK(200, "OK"),
  BAD_REQUEST(400, "Bad Request"),
  METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
  INTERNAL_SERVER_ERROR(500, "Internal Server Error");

  private final int code;
  private final String reason;

  HttpStatus(int code, String reason) {
    this.code = code;
    this.reason = reason;
  }

  public int code() {
    return code;
  }

  public String reason() {
    return reason;
  }

  public static HttpStatus fromCode(int code) {
    for (HttpStatus s : values()) {
      if (s.code == code) return s;
    }
    return INTERNAL_SERVER_ERROR; // дефолт
  }
}
