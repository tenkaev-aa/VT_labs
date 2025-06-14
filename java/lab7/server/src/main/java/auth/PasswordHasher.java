package auth;

import database.DatabaseManager;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordHasher {

  private static final int salt_length = DatabaseManager.getInt("salt.length", 8);
  private static final int iterations = DatabaseManager.getInt("hash.iterations", 1488);

  public static byte[] generateSalt() {
    byte[] salt = new byte[salt_length];
    new SecureRandom().nextBytes(salt);
    return salt;
  }

  public static String hash(String password, byte[] salt) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-384");

      byte[] input = concat(password.getBytes(StandardCharsets.UTF_8), salt);
      byte[] result = digest.digest(input);

      for (int i = 0; i < iterations - 1; i++) {
        result = digest.digest(result);
      }

      return bytesToHex(result);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-384 не найден", e);
    }
  }

  private static byte[] concat(byte[] a, byte[] b) {
    byte[] combined = new byte[a.length + b.length];
    System.arraycopy(a, 0, combined, 0, a.length);
    System.arraycopy(b, 0, combined, a.length, b.length);
    return combined;
  }

  public static String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }

  public static byte[] hexToBytes(String hex) {
    int len = hex.length();
    byte[] result = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      result[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
    }
    return result;
  }
}
