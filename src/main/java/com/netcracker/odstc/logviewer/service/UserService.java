package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.dao.UserDao;
import com.netcracker.odstc.logviewer.models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
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

    public UserService(@Qualifier("userDao") UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getUsers() {
        return userDao.getObjectsByObjectTypeId(BigInteger.ONE, User.class);
    }

    public Page<User> getUsers(Pageable page) {
        return new PageImpl<User>(userDao.getUsers(page));
    }

    public User findById(BigInteger id) {
        if (isIdValid(id)) {
            return userDao.getObjectById(id, User.class);
        }
        return null;
    }

    public User findByLogin(String login) {
        if (login != null) {
            return userDao.getByLogin(login);
        }
        logger.error("login cant be null.");
        return null;
    }

    public boolean update(User user) {
        if (isUserValid(user)) {
            userDao.saveObjectAttributesReferences(user);
            return true;
        }
        return false;
    }

    public boolean updatePassword(User user) {
        if(user.getPassword() != null && user.getLogin() != null)
        {
            User userUpdate = userDao.getByLogin(user.getLogin());
            if(userUpdate != null){
                userUpdate.setPassword(user.getPassword());
                return update(userUpdate);
            }
        }
        logger.error("User is not valid to update password.");
        return false;
    }

    public boolean save(User user) {
        if (isUserValidForSave(user)) {
            userDao.saveObjectAttributesReferences(user);
            return true;
        }
        return false;
    }

    public boolean deleteById(BigInteger id) {
        if (isIdValid(id)) {
            userDao.deleteById(id);
            return true;
        }
        return false;
    }

    private boolean isIdValid(BigInteger id) {
        boolean isIdValid = id != null && !id.equals(BigInteger.valueOf(0));
        if(!isIdValid){
            logger.error("user id is not valid.");
        }
        return isIdValid;
    }

    private boolean isUserValid(User user) {
        if (user != null &&
                user.getLogin() != null &&
                user.getPassword() != null &&
                user.getRole() != null) {
            return true;
        }
        logger.error("User is not valid.");
        return false;
    }

    private boolean isUserValidForSave(User user) {
        boolean isUserValidForSave = userDao.getByLogin(user.getLogin()) == null && isUserValid(user);
        if(!isUserValidForSave){
            logger.error("user is not valid for save.");
        }
        return isUserValidForSave;
    }
}
