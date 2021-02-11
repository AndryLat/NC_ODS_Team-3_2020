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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @PutMapping("/promoteToAdmin")
    public ResponseEntity<User> promoteToAdmin(@RequestBody User user) {
        logger.info("PUT: Requested promote user with id {} to admin.", (user.getObjectId() != null ? user.getObjectId() : "null"));
        userService.updateRole(user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<User> updatePassword(@RequestBody User user) {
        logger.info("PUT: Requested update password for user with id {}", (user.getObjectId() != null ? user.getObjectId() : "null"));
        userService.updatePassword(user);
        return ResponseEntity.noContent().build();
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
        String login = securityService.getLogin(token, id);
        logger.info("GET: Requested check resetPasswordToken for user with login {}", (login != null ? login : "null"));
        return ResponseEntity.ok(login);
    }

    @PostMapping("/checkPassword")
    public ResponseEntity<Boolean> checkPassword(@RequestBody User user) {
        return ResponseEntity.ok(userService.checkPassword(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable BigInteger id) {
        logger.info("GET: Requested user with id {}", (id != null ? id : "null"));
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/getInfo")
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        User currentUser = userService.findByLogin(principal.getName());
        return ResponseEntity.ok(currentUser);
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
