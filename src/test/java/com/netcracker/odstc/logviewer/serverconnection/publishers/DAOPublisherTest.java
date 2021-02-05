package com.netcracker.odstc.logviewer.serverconnection.publishers;

import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.models.eaventity.constants.ObjectTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DAOPublisherTest {

    DAOPublisher daoPublisher;

    @BeforeEach
    void setUp() {
        daoPublisher = DAOPublisher.getInstance();
    }

    @Test
    void notifyListeners_GetNewEvents_EventsRecorded() {

        TestDAOListener directoryListener = new TestDAOListener();
        TestDAOListener serverListener = new TestDAOListener();
        daoPublisher.addListener(directoryListener, ObjectTypes.DIRECTORY);
        daoPublisher.addListener(serverListener, ObjectTypes.SERVER);

        Server server = new Server();
        Server secondServer = new Server(BigInteger.valueOf(1));
        Directory directory = new Directory();

        daoPublisher.notifyListeners(new ObjectChangeEvent(ObjectChangeEvent.ChangeType.UPDATE,this,server,null),ObjectTypes.SERVER);
        daoPublisher.notifyListeners(new ObjectChangeEvent(ObjectChangeEvent.ChangeType.DELETE,this,secondServer,null),ObjectTypes.SERVER);
        daoPublisher.notifyListeners(new ObjectChangeEvent(ObjectChangeEvent.ChangeType.UPDATE,this,directory,null),ObjectTypes.DIRECTORY);

        assertNotNull(directoryListener.getObjectChangeEventList());
        assertNotNull(serverListener.getObjectChangeEventList());

        assertEquals(1,directoryListener.getObjectChangeEventList().size());
        assertEquals(2,serverListener.getObjectChangeEventList().size());

        assertNotNull(directoryListener.getObjectChangeEventList().get(0));
        assertNotNull(serverListener.getObjectChangeEventList().get(0));
        assertNotNull(serverListener.getObjectChangeEventList().get(1));

        assertEquals(directory,directoryListener.getObjectChangeEventList().get(0).getObject());
        assertEquals(server,serverListener.getObjectChangeEventList().get(0).getObject());
        assertEquals(secondServer,serverListener.getObjectChangeEventList().get(1).getObject());
    }



    class TestDAOListener implements DAOChangeListener {

        private List<ObjectChangeEvent> objectChangeEventList;

        public List<ObjectChangeEvent> getObjectChangeEventList() {
            return objectChangeEventList;
        }

        public void setObjectChangeEventList(List<ObjectChangeEvent> objectChangeEventList) {
            this.objectChangeEventList = objectChangeEventList;
        }

        public TestDAOListener(){
            objectChangeEventList = new ArrayList<>();
        }
        @Override
        public void objectChanged(ObjectChangeEvent objectChangeEvent) {
            objectChangeEventList.add(objectChangeEvent);
        }
    }
}
