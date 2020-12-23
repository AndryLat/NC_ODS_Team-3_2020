package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.dao.UserDao;
import com.netcracker.odstc.logviewer.models.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class UserService {

    private final UserDao userDao;

    public UserService(@Qualifier("userDao") UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getUsers() {
        return userDao.getObjectsByObjectTypeId(BigInteger.ONE, User.class);
    }

    public Page<User> getUsers(Pageable page) {
        return new PageImpl<User>(userDao.getUsers(page),
                page,userDao.getUserCount());
    }

    public User findById(BigInteger id) {
        if (isIdValid(id)) {
            return userDao.getObjectById(id, User.class);
        }
        return null;
    }

    public User findByName(String name) {
        if (name != null) {
            return userDao.getByName(name);
        }
        return null;
    }

    public void update(User user) {
        if (isUserValid(user)) {
            userDao.saveObjectAttributesReferences(user);
        }
    }

    public void save(User user) {
        if (isUserValidForSave(user)) {
            userDao.saveObjectAttributesReferences(user);
        }
    }


    public void deleteById(BigInteger id) {
        if (isIdValid(id)) {
            userDao.deleteById(id);
        }
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
        return userDao.getByName(user.getLogin()) == null && isUserValid(user);
    }
}
