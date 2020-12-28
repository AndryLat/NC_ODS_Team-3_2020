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
@RequestMapping("/user")
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

    @GetMapping("/all")
    public Page<User> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        PageRequest pageable = PageRequest.of(page, pageSize);
        return userService.getUsers(pageable);
    }

    @PostMapping("/create")
    public ResponseEntity<User> create(@RequestBody User user, Principal principal) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        User creator = userService.findByLogin(principal.getName());
        user.setCreated(creator.getObjectId());
        boolean result = userService.save(user);
        if(result){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity("User is not valid to create", HttpStatus.NOT_ACCEPTABLE);
    }

    @PutMapping("/update")
    public ResponseEntity<User> update(@RequestBody User user) {
        if (user == null) {
            return new ResponseEntity("User shouldn't be null", HttpStatus.NOT_ACCEPTABLE);
        }
        userService.update(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<User> updatePassword(@RequestBody User user) {
        if (user == null) {
            return new ResponseEntity("User shouldn't be null", HttpStatus.NOT_ACCEPTABLE);
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        boolean result = userService.updatePassword(user);
        if(result){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity("User is not valid to update", HttpStatus.NOT_ACCEPTABLE);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<User> resetPassword(HttpServletRequest request,
                                @RequestParam("login") String login) {
        User user = userService.findByLogin(login);
        if (user == null) {
            return new ResponseEntity("User is not found", HttpStatus.NOT_ACCEPTABLE);
        }
        String token = securityService.createPasswordResetTokenForUser(user);
        mailSender.send(mailService.constructResetTokenEmail(securityService.getAppUrl(request), token, user));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/changePassword")
    public ResponseEntity<User> changePassword(@RequestParam("id") long id, @RequestParam("token") String token) {
        if(securityService.validatePasswordResetToken(token)){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity("Token is not available", HttpStatus.NOT_ACCEPTABLE);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable BigInteger id) {
        if (id == null || id.equals(BigInteger.valueOf(0))) {
            return new ResponseEntity("Id shouldn't be 0 or null", HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(userService.findById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<User> deleteById(@PathVariable BigInteger id) {
        if (id == null || id.equals(BigInteger.valueOf(0))) {
            return new ResponseEntity("Id shouldn't be 0 or null", HttpStatus.NOT_ACCEPTABLE);
        }
        boolean result = userService.deleteById(id);
        if(result){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity("User is not valid to delete", HttpStatus.NOT_ACCEPTABLE);
    }

}
