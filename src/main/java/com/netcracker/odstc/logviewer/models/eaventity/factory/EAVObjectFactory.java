package com.netcracker.odstc.logviewer.models.eaventity.factory;

import com.netcracker.odstc.logviewer.models.Config;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.models.User;
import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import com.netcracker.odstc.logviewer.models.eaventity.constants.ObjectTypes;
import com.netcracker.odstc.logviewer.models.eaventity.factory.exceptions.UnsupportedObjectTypeException;

import java.math.BigInteger;

public class EAVObjectFactory {

    private static EAVObjectFactory eavObjectFactory;

    private EAVObjectFactory() {
    }

    public static EAVObjectFactory getInstance() {
        if (eavObjectFactory == null) {
            eavObjectFactory = new EAVObjectFactory();
        }
        return eavObjectFactory;
    }

    public <T extends EAVObject> T createEAVObject(BigInteger objectTypeId) {
        return createEAVObject(ObjectTypes.getObjectTypesByObjectTypeId(objectTypeId));
    }

    public <T extends EAVObject> T createEAVObject(ObjectTypes objectType) {
        return (T) createEAVObject(objectType.getObjectClass());
    }

    public <T extends EAVObject> T createEAVObject(BigInteger objectId, Class<T> clazz) {
        EAVObject eavObject = createEAVObject(clazz);
        eavObject.setObjectId(objectId);
        return (T) eavObject;
    }

    public <T extends EAVObject> T createEAVObject(Class<T> clazz) {
        EAVObject eavObject;
        if (Log.class.isAssignableFrom(clazz)) {
            eavObject = new Log();
        } else if (LogFile.class.isAssignableFrom(clazz)) {
            eavObject = new LogFile();
        } else if (Directory.class.isAssignableFrom(clazz)) {
            eavObject = new Directory();
        } else if (Server.class.isAssignableFrom(clazz)) {
            eavObject = new Server();
        } else if (User.class.isAssignableFrom(clazz)) {
            eavObject = new User();
        } else if (Config.class.isAssignableFrom(clazz)) {
            eavObject = new Config();
        } else {
            throw new UnsupportedObjectTypeException("Cant instantinate object for class: " + clazz.getName());
        }
        return (T) eavObject;
    }
}
