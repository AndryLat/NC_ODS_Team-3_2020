package com.netcracker.odstc.logviewer.controller;

import com.netcracker.odstc.logviewer.models.Config;
import com.netcracker.odstc.logviewer.models.User;
import com.netcracker.odstc.logviewer.service.SecurityService;
import com.netcracker.odstc.logviewer.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.security.Principal;


@RestController
@RequestMapping("api/user")
public class UserController {
    private final Logger logger = LogManager.getLogger(UserController.class);

    private static final String DEFAULT_PAGE_SIZE = "10";

    private UserService userService;
    private SecurityService securityService;

    public UserController(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    @GetMapping("/")
    public Page<User> getUsers(@RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        PageRequest pageable = PageRequest.of(page - 1, pageSize);
        logger.info("GET: Requested all users");
        return userService.getUsers(pageable);
    }

    @PostMapping("/create")
    public ResponseEntity<User> create(@RequestBody User user, Principal principal) {
        userService.create(user, principal.getName());
        logger.info("POST: Requested create user with id:{} and role:{}", (user.getObjectId() != null ? user.getObjectId() : "null"), (user.getRole() != null ? user.getRole() : "null"));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update")
    public ResponseEntity<User> update(@RequestBody User user) {
        logger.info("PUT: Requested update for user with id {}", (user.getObjectId() != null ? user.getObjectId() : "null"));
        userService.update(user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<User> updatePassword(@RequestBody User user) {
        logger.info("PUT: Requested update password for user with id {}", (user.getObjectId() != null ? user.getObjectId() : "null"));
        userService.updatePassword(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<User> resetPassword(HttpServletRequest request,
                                              @RequestBody String login) {
        logger.info("POST: Requested reset password for user with login {}", (login != null ? login : "null"));
        User user = userService.findByLogin(login);
        String token = securityService.createPasswordResetTokenForUser(user);
        userService.sendResetToken(securityService.getAppUrl(request), token, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestParam("id") BigInteger id, @RequestParam("token") String token) {
        if (securityService.validatePasswordResetToken(token, id)) {
            userService.throwException("Password reset is not available.");
        }
        String login = securityService.getLoginUserFromToken(token);
        logger.info("GET: Requested check resetPasswordToken for user with login {}", (login != null ? login : "null"));
        return ResponseEntity.ok(login);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable BigInteger id) {
        logger.info("GET: Requested user with id {}", (id != null ? id : "null"));
        return ResponseEntity.ok(userService.findById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<User> deleteById(@PathVariable BigInteger id) {
        logger.info("DELETE: Requested deleting for user id {}", (id != null ? id : "null"));
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/updateConfig")
    public ResponseEntity<Config> updateSettings(@RequestBody Config config) {
        logger.info("POST: Requested update config");
        userService.saveConfig(config);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/config")
    public ResponseEntity<Config> getConfig() {
        logger.info("GET: Requested config");
        return ResponseEntity.ok(userService.getConfig());
    }
}
