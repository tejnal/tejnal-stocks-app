package com.tejnal.stockexchange.controller;

import com.tejnal.stockexchange.data.entity.RefreshToken;
import com.tejnal.stockexchange.data.entity.Role;
import com.tejnal.stockexchange.data.entity.User;
import com.tejnal.stockexchange.data.enums.ERole;
import com.tejnal.stockexchange.data.repository.RoleRepository;
import com.tejnal.stockexchange.data.repository.UserRepository;
import com.tejnal.stockexchange.model.request.LoginRequest;
import com.tejnal.stockexchange.model.request.SignupRequest;
import com.tejnal.stockexchange.model.request.TokenRefreshRequest;
import com.tejnal.stockexchange.model.response.JwtResponse;
import com.tejnal.stockexchange.model.response.MessageResponse;
import com.tejnal.stockexchange.model.response.TokenRefreshResponse;
import com.tejnal.stockexchange.security.exception.TokenRefreshException;
import com.tejnal.stockexchange.security.jwt.JwtUtils;
import com.tejnal.stockexchange.security.services.RefreshTokenService;
import com.tejnal.stockexchange.security.services.UserDetailsImpl;
import com.tejnal.stockexchange.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private static final String SUCCESS = "SUCCESS";
  private static final String ERROR = "ERROR";
  private static final String USER_NOT_FOUND = "user is not found!";

  @Autowired AuthenticationManager authenticationManager;

  @Autowired UserRepository userRepository;

  @Autowired RoleRepository roleRepository;

  @Autowired PasswordEncoder encoder;

  @Autowired JwtUtils jwtUtils;

  @Autowired RefreshTokenService refreshTokenService;

  /**
   * @param loginRequest
   * @return
   */
  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    String jwt = jwtUtils.generateJwtToken(userDetails);
    List<String> roles =
        userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
    return ResponseEntity.ok(
        new JwtResponse(
            jwt,
            refreshToken.getToken(),
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getEmail(),
            roles,
            userDetails.getFirstName(),
            userDetails.getLastName()));
  }

  /**
   * @param request
   * @return
   */
  @PostMapping("/refreshtoken")
  public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
    String requestRefreshToken = request.getRefreshToken();
    return refreshTokenService
        .findByToken(requestRefreshToken)
        .map(refreshTokenService::verifyExpiration)
        .map(RefreshToken::getUser)
        .map(
            user -> {
              String token = jwtUtils.generateTokenFromUsername(user.getUsername());
              return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
            })
        .orElseThrow(
            () ->
                new TokenRefreshException(
                    requestRefreshToken, "Refresh token is not in database!"));
  }

  /**
   * @param request
   * @return
   */
  @GetMapping("/logout")
  public ResponseEntity<?> logoutUser(HttpServletRequest request) {
    UserDetails userDetails =
        (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User loggedInUser = userRepository.findByUsername(userDetails.getUsername()).get();
    refreshTokenService.deleteByUserId(loggedInUser.getId());
    jwtUtils.invalidateJwtToken(Utils.parseJwt(request));
    return ResponseEntity.ok(new MessageResponse("SUCCESS", "Log out successful!"));
  }

  /**
   * @param signUpRequest
   * @return
   */
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse("ERROR", "Username is already taken."));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse("ERROR", "Email is already in use."));
    }

    // Create new user's account
    User user =
        new User(
            signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()),
            signUpRequest.getFirstName(),
            signUpRequest.getLastName());

    Set<Role> roles = new HashSet<>();
    // Always User role for user sign up
    Role userRole =
        roleRepository
            .findByName(ERole.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    roles.add(userRole);

    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("SUCCESS", "User registered successfully."));
  }

  /**
   * @param signUpRequest
   * @return
   */
  @PostMapping("/admin/signup")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> registerUserByAdmin(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse("ERROR", "Username is already taken."));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse("ERROR", "Email is already in use."));
    }

    // Create new user's account
    User user =
        new User(
            signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()),
            signUpRequest.getFirstName(),
            signUpRequest.getLastName());

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole =
          roleRepository
              .findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(
          role -> {
            switch (role) {
              case "admin":
                Role adminRole =
                    roleRepository
                        .findByName(ERole.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(adminRole);
                break;
              default:
                Role defaultRole =
                    roleRepository
                        .findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(defaultRole);
            }
          });
    }

    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("SUCCESS", "User registered successfully."));
  }
}
