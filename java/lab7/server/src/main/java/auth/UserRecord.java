package auth;

public record UserRecord(int id, String passwordHash, String saltHex) {}
