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
public class UserService extends AbstractService {
    private static final String LOGIN_NOT_FOUND_MESSAGE = "User by login not found.";
    private static final String USER_IS_NULL_MESSAGE = "User shouldn't be null.";
    private static final String USER_INVALID_LOGIN_MESSAGE = "User don't have unique login.";
    private final Logger logger = LogManager.getLogger(UserService.class.getName());
    private final UserDao userDao;
    private final EAVObjectDAO eavObjectDAO;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MailService mailService;
    private final JavaMailSender mailSender;

    public UserService(UserDao userDao, EAVObjectDAO eavObjectDAO, BCryptPasswordEncoder bCryptPasswordEncoder, JavaMailSender mailSender, MailService mailService) {
        this.userDao = userDao;
        this.eavObjectDAO = eavObjectDAO;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.mailSender = mailSender;
        this.mailService = mailService;
    }

    public Page<User> getUsers(Pageable page) {
        return eavObjectDAO.getObjectsByObjectTypeId(page, BigInteger.ONE, User.class);
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
            throwUserServiceExceptionWithMessage(LOGIN_NOT_FOUND_MESSAGE);
        }
        return user;
    }

    public void create(User user, String login) {
        if (user == null) {
            throwUserServiceExceptionWithMessage(USER_IS_NULL_MESSAGE);
        } else {
            if (checkUniqueLogin(user)) {
                user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                User creator = findByLogin(login);
                user.setCreated(creator.getObjectId());
                save(user);
            } else {
                throwUserServiceExceptionWithMessage(USER_INVALID_LOGIN_MESSAGE);
            }
        }
    }

    private boolean checkUniqueLogin(User user) {
        try {
            this.findByLogin(user.getLogin());
            return false;
        } catch (UserServiceException exp) {
            return true;
        }
    }

    public void update(User user) {
        if (!isUserValid(user)) {
            throwUserServiceExceptionWithMessage("User is not valid.");
        }
        userDao.saveObjectAttributesReferences(user);
    }

    public void updateRole(User user) {
        if (user == null) {
            throwUserServiceExceptionWithMessage(USER_IS_NULL_MESSAGE);
        } else {
            if (!isIdValid(user.getObjectId()) || user.getRole() == null) {
                throwUserServiceExceptionWithMessage("User is not valid.");
            }
            User userFromDb = userDao.getByLogin(user.getLogin());
            if (userFromDb == null) {
                throwUserServiceExceptionWithMessage(LOGIN_NOT_FOUND_MESSAGE);
            } else {
                userFromDb.setRole(user.getRole());
                update(userFromDb);
            }
        }
    }

    public void updatePassword(User user) {
        if (user == null) {
            throwUserServiceExceptionWithMessage(USER_IS_NULL_MESSAGE);
        } else {
            if (user.getPassword() == null || user.getLogin() == null) {
                throwUserServiceExceptionWithMessage("User is not valid to update password.");
            }
            User userFromDb = userDao.getByLogin(user.getLogin());
            if (userFromDb == null) {
                throwUserServiceExceptionWithMessage(LOGIN_NOT_FOUND_MESSAGE);
            } else {
                userFromDb.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                update(userFromDb);
            }
        }
    }

    public boolean checkPassword(User user) {
        if (user == null) {
            throwUserServiceExceptionWithMessage(USER_IS_NULL_MESSAGE);
            return false;
        } else {
            if (user.getPassword() == null || user.getLogin() == null) {
                throwUserServiceExceptionWithMessage("User is not valid to check password.");
                return false;
            }
            User userFromDb = userDao.getByLogin(user.getLogin());
            if (userFromDb == null) {
                throwUserServiceExceptionWithMessage(LOGIN_NOT_FOUND_MESSAGE);
                return false;
            } else {
                return bCryptPasswordEncoder.matches(user.getPassword(), userFromDb.getPassword());
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
            throwUserServiceExceptionWithMessage("Config cant be save.");
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

    private void save(User user) {
        if (!isUserValidForSave(user)) {
            throwUserServiceExceptionWithMessage("User is not valid for save.");
        }
        userDao.saveObjectAttributesReferences(user);
    }

    private boolean isUserValid(User user) {
        return user != null &&
                user.getLogin() != null &&
                user.getEmail() != null &&
                user.getPassword() != null &&
                user.getRole() != null;
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
