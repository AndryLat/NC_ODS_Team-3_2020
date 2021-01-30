package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.models.User;
import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import com.netcracker.odstc.logviewer.models.eaventity.constants.ObjectTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;

public abstract class AbstractService {
    private static final Logger logger = LogManager.getLogger(AbstractService.class.getName());

    protected boolean isIdValid(BigInteger id) {
        return id != null && !id.equals(BigInteger.valueOf(0));
    }

    protected void validateObjectType(EAVObject eavObject) {
        if (eavObject instanceof Directory) {
            eavObject.setObjectTypeId(ObjectTypes.DIRECTORY.getObjectTypeID());
        } else if (eavObject instanceof LogFile) {
            eavObject.setObjectTypeId(ObjectTypes.LOGFILE.getObjectTypeID());
        } else if (eavObject instanceof User) {
            eavObject.setObjectTypeId(ObjectTypes.USER.getObjectTypeID());
        } else if (eavObject instanceof Server) {
            eavObject.setObjectTypeId(ObjectTypes.SERVER.getObjectTypeID());
        } else if (eavObject instanceof Log) {
            eavObject.setObjectTypeId(ObjectTypes.LOG.getObjectTypeID());
        } else {
            logger.warn("Get EAVObject that not listed in validations");
        }
    }
}
