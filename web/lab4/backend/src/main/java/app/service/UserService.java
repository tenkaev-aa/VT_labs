package app.service;

import app.entity.UserEntity;
import app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserEntity registerUser(String username, String rawPassword) {
    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("User with this username already exists");
    }

    String encodedPassword = passwordEncoder.encode(rawPassword);

    UserEntity user = UserEntity.builder().username(username).passwordHash(encodedPassword).build();

    return userRepository.save(user);
  }

  public UserEntity findByUsernameOrThrow(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
  }
}
