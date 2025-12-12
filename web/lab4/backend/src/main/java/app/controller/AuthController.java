package app.controller;

import app.dto.auth.AuthRequest;
import app.dto.auth.AuthResponse;
import app.entity.UserEntity;
import app.security.CustomUserDetails;
import app.security.JwtUtil;
import app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final UserService userService;
  private final JwtUtil jwtUtil;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody AuthRequest request) {
    try {
      UserEntity created = userService.registerUser(request.getUsername(), request.getPassword());
      return ResponseEntity.ok("User created: " + created.getUsername());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequest request) {
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  request.getUsername(), request.getPassword()));
      Object principal = authentication.getPrincipal();
      UserDetails userDetails;
      Long userId;

      if (principal instanceof CustomUserDetails custom) {
        userDetails = custom;
        userId = custom.getId();
      } else if (principal instanceof UserDetails ud) {
        userDetails = ud;
        UserEntity user = userService.findByUsernameOrThrow(ud.getUsername());
        userId = user.getId();
      } else {
        throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
      }

      String token = jwtUtil.generateToken(userDetails, userId);

      return ResponseEntity.ok(new AuthResponse(token));

    } catch (BadCredentialsException e) {
      return ResponseEntity.status(401).body("Invalid username or password");
    }
  }
}
