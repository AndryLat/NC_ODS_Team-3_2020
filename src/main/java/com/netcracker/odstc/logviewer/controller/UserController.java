package com.netcracker.odstc.logviewer.controller;

import com.netcracker.odstc.logviewer.models.User;
import com.netcracker.odstc.logviewer.service.MailService;
import com.netcracker.odstc.logviewer.service.SecurityService;
import com.netcracker.odstc.logviewer.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.security.Principal;


@RestController
@RequestMapping("api/user")
public class UserController {

    private static final String DEFAULT_PAGE_SIZE = "10";

    private UserService userService;
    private SecurityService securityService;
    private MailService mailService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private JavaMailSender mailSender;

    public UserController(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder, JavaMailSender mailSender, SecurityService securityService, MailService mailService) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.mailSender = mailSender;
        this.securityService = securityService;
        this.mailService = mailService;
    }

    @GetMapping("/")
    public Page<User> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        PageRequest pageable = PageRequest.of(page, pageSize);
        return userService.getUsers(pageable);
    }

    @PostMapping("/create")
    public ResponseEntity<User> create(@RequestBody User user, Principal principal) {
        if (user == null) {
            userService.throwException("User shouldn't be null");
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        User creator = userService.findByLogin(principal.getName());
        user.setCreated(creator.getObjectId());
        userService.save(user);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PutMapping("/update")
    public ResponseEntity<User> update(@RequestBody User user) {
        userService.update(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<User> updatePassword(@RequestBody User user) {
        if (user == null) {
            userService.throwException("User shouldn't be null");
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userService.updatePassword(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<User> resetPassword(HttpServletRequest request,
                                @RequestBody String login) {
        User user = userService.findByLogin(login);
        String token = securityService.createPasswordResetTokenForUser(user);
        mailSender.send(mailService.constructResetTokenEmail(securityService.getAppUrl(request), token, user));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestParam("id") BigInteger id, @RequestParam("token") String token) {
        if(securityService.validatePasswordResetToken(token, id)){
            userService.throwException("Password reset is not available.");
        }
        String login = securityService.getLoginUserFromToken(token);
        return ResponseEntity.ok(login);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable BigInteger id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<User> deleteById(@PathVariable BigInteger id) {
        userService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
