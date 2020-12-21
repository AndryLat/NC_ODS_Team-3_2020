package com.netcracker.odstc.logviewer.controller;

import com.netcracker.odstc.logviewer.models.User;
import com.netcracker.odstc.logviewer.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserController(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PostMapping("/create")
    public ResponseEntity<User> create(@RequestBody User user, Principal principal) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        User creator = userService.findByName(principal.getName());
        user.setCreated(creator.getObjectId());
        userService.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<User> update(@RequestBody User user) {
        if (user == null) {
            return new ResponseEntity("User shouldn't be null", HttpStatus.NOT_ACCEPTABLE);
        }
        userService.update(user);
        return new ResponseEntity<>(HttpStatus.OK);
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
        userService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
