package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.dao.EAVObjectDAO;
import com.netcracker.odstc.logviewer.dao.UserDao;
import com.netcracker.odstc.logviewer.models.Config;
import com.netcracker.odstc.logviewer.models.User;
import com.netcracker.odstc.logviewer.models.eaventity.constants.ObjectTypes;
import com.netcracker.odstc.logviewer.service.exceptions.UserServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class UserService {
    private final Logger logger = LogManager.getLogger(UserService.class.getName());

    private final UserDao userDao;
    private final EAVObjectDAO eavObjectDAO;

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private MailService mailService;
    private JavaMailSender mailSender;

    public UserService(UserDao userDao, EAVObjectDAO eavObjectDAO, BCryptPasswordEncoder bCryptPasswordEncoder, JavaMailSender mailSender, MailService mailService) {
        this.userDao = userDao;
        this.eavObjectDAO = eavObjectDAO;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.mailSender = mailSender;
        this.mailService = mailService;
    }

    public Page<User> getUsers(Pageable page) {
        return new PageImpl<>(userDao.getUsers(page));
    }

    public User findById(BigInteger id) {
        if (!isIdValid(id)) {
            throwUserServiceExceptionWithMessage("User could not be found, user id is invalid.");
        }
        return userDao.getObjectById(id, User.class);
    }

    public User findByLogin(String login) {
        if (login == null || login.isEmpty()) {
            throwUserServiceExceptionWithMessage("Login cant be null.");
        }
        User user = userDao.getByLogin(login);
        if (user == null) {
            throwUserServiceExceptionWithMessage("User by login not found.");
        }
        return user;
    }

    public void create(User user, String login) {
        if (user == null) {
            throwUserServiceExceptionWithMessage("User shouldn't be null.");
        } else {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            User creator = findByLogin(login);
            user.setCreated(creator.getObjectId());
            save(user);
        }
    }

    public void update(User user) {
        if (!isUserValid(user)) {
            throwUserServiceExceptionWithMessage("User is not valid.");
        }
        userDao.saveObjectAttributesReferences(user);
    }

    public void updatePassword(User user) {
        if (user == null) {
            throwUserServiceExceptionWithMessage("User shouldn't be null.");
        } else {
            if (user.getPassword() == null && user.getLogin() == null) {
                throwUserServiceExceptionWithMessage("User is not valid to update password.");
            }
            User userFromDb = userDao.getByLogin(user.getLogin());
            if (userFromDb == null) {
                throwUserServiceExceptionWithMessage("User by login not found.");
            } else {
                userFromDb.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                update(userFromDb);
            }
        }
    }

    public boolean checkPassword(User user) {
        if (user == null) {
            throwUserServiceExceptionWithMessage("User shouldn't be null.");
            return false;
        } else {
            if (user.getPassword() == null && user.getLogin() == null) {
                throwUserServiceExceptionWithMessage("User is not valid to check password.");
                return false;
            }
            User userFromDb = userDao.getByLogin(user.getLogin());
            if (userFromDb == null) {
                throwUserServiceExceptionWithMessage("User by login not found.");
                return false;
            } else {
                return bCryptPasswordEncoder.matches(user.getPassword(), bCryptPasswordEncoder.encode(userFromDb.getPassword()));
            }
        }
    }

    public void sendResetToken(String appUrl, String token, User user) {
        mailSender.send(mailService.constructResetTokenEmail(appUrl, token, user));
    }

    public void deleteById(BigInteger id) {
        if (!isIdValid(id)) {
            throwUserServiceExceptionWithMessage("User cannot be deleted, user id is invalid.");
        }
        userDao.deleteById(id);
    }

    public void saveConfig(Config config) {
        if (config == null) {
            throwException("Config cant be save.");
        } else {
            logger.info(config);
            config.setObjectId(BigInteger.ZERO);
            config.setObjectTypeId(ObjectTypes.CONFIG.getObjectTypeID());
            eavObjectDAO.saveObjectAttributesReferences(config);
            Config.setInstance(config);
        }
    }

    public Config getConfig() {
        if (Config.getInstance() == null) {
            return eavObjectDAO.getObjectById(BigInteger.ZERO, Config.class);
        } else {
            return Config.getInstance();
        }
    }

    public void throwException(String nameException) {
        IllegalArgumentException exception = new IllegalArgumentException();
        logger.error(nameException);
        throw exception;
    }

    private void save(User user) {
        if (!isUserValidForSave(user)) {
            throwUserServiceExceptionWithMessage("User is not valid for save.");
        }
        userDao.saveObjectAttributesReferences(user);
    }

    private boolean isIdValid(BigInteger id) {
        return id != null && !id.equals(BigInteger.valueOf(0));
    }

    private boolean isUserValid(User user) {
        if (user != null &&
                user.getLogin() != null &&
                user.getEmail() != null &&
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
}
