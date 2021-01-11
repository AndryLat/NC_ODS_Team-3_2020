package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.dao.UserDao;
import com.netcracker.odstc.logviewer.models.User;
import com.netcracker.odstc.logviewer.service.exceptions.UserServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class UserService {

    private final Logger logger = LogManager.getLogger(UserService.class.getName());
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getUsers() {
        return userDao.getObjectsByObjectTypeId(BigInteger.ONE, User.class);
    }

    public Page<User> getUsers(Pageable page) {
        return new PageImpl<User>(userDao.getUsers(page));
    }

    public User findById(BigInteger id) {
        if (!isIdValid(id)) {
            throwUserServiceExceptionWithMessage("User could not be found, user id is invalid.");
        }
        return userDao.getObjectById(id, User.class);
    }

    public User findByLogin(String login) {
        if (login == null || login.equals("")) {
            throwUserServiceExceptionWithMessage("Login cant be null.");
        }
        User user = userDao.getByLogin(login);
        if(user == null){
            throwUserServiceExceptionWithMessage("User by login not found.");
        }
        return user;
    }

    public void update(User user) {
        if (!isUserValid(user)) {
            throwUserServiceExceptionWithMessage("User is not valid");
        }
        userDao.saveObjectAttributesReferences(user);
    }

    public void updatePassword(User user) {
        if(user.getPassword() == null && user.getLogin() == null)
        {
            throwUserServiceExceptionWithMessage("User is not valid to update password.");
        }
        User userFromDb = userDao.getByLogin(user.getLogin());
        if(userFromDb == null){
            throwUserServiceExceptionWithMessage("User by login not found.");
        }
        userFromDb.setPassword(user.getPassword());
        update(userFromDb);
    }

    public void save(User user) {
        if (!isUserValidForSave(user)) {
            throwUserServiceExceptionWithMessage("User is not valid for save.");
        }
        userDao.saveObjectAttributesReferences(user);
    }

    public void deleteById(BigInteger id) {
        if (!isIdValid(id)) {
            throwUserServiceExceptionWithMessage("User cannot be deleted, user id is invalid.");
        }
        userDao.deleteById(id);
    }

    private boolean isIdValid(BigInteger id) {
        return id != null && !id.equals(BigInteger.valueOf(0));
    }

    private boolean isUserValid(User user) {
        if (user != null &&
                user.getLogin() != null &&
                user.getPassword() != null &&
                user.getRole() != null) {
            return true;
        }
        return false;
    }

    private boolean isUserValidForSave(User user) {
        return isUserValid(user) && userDao.getByLogin(user.getLogin()) == null;
    }

    private void throwUserServiceExceptionWithMessage(String message) {
        UserServiceException userServiceException = new UserServiceException(message);
        logger.error(message, userServiceException);
        throw userServiceException;
    }

    public void throwException(String nameException) {
        IllegalArgumentException exception = new IllegalArgumentException();
        logger.error(nameException);
        throw exception;
    }
}
