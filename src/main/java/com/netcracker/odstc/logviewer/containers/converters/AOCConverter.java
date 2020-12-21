package com.netcracker.odstc.logviewer.containers.converters;

import com.netcracker.odstc.logviewer.containers.AttributeObjectContainer;
import com.netcracker.odstc.logviewer.containers.HierarchyContainer;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AOCConverter {

    public Map<BigInteger, HierarchyContainer> convertAOCtoHC(List<AttributeObjectContainer> attributeObjectContainerList) {

        Map<BigInteger, HierarchyContainer> eavObjectList = new HashMap<>(attributeObjectContainerList.size());
        for (AttributeObjectContainer attributeObjectContainer : attributeObjectContainerList) {

            BigInteger objectId = attributeObjectContainer.getObjectId();
            if (eavObjectList.containsKey(objectId)) {
                appendAttribute(attributeObjectContainer, eavObjectList.get(objectId).getOriginal());
                continue;
            }
            BigInteger objectTypeId = attributeObjectContainer.getObjectTypeId();

            EAVObject eavObject;
            if (objectTypeId.equals(BigInteger.valueOf(3))) {
                eavObject = new Directory();
            } else if (objectTypeId.equals(BigInteger.valueOf(4))) {
                eavObject = new LogFile();
            } else if (objectTypeId.equals(BigInteger.valueOf(2))) {
                eavObject = new Server();
            } else {
                throw new IllegalArgumentException();
            }
            eavObject.setParentId(attributeObjectContainer.getParentId());

            eavObject.setObjectTypeId(objectTypeId);
            eavObject.setName(attributeObjectContainer.getName());
            eavObject.setObjectId(objectId);
            eavObjectList.put(objectId, new HierarchyContainer(eavObject));
            appendAttribute(attributeObjectContainer, eavObject);
        }

        setUpHierarchy(eavObjectList);

        return eavObjectList;
    }

    public void setUpHierarchy(Map<BigInteger, HierarchyContainer> eavObjectList) {

        for (Map.Entry<BigInteger, HierarchyContainer> eavObject : eavObjectList.entrySet()) {
            if (eavObjectList.containsKey(eavObject.getValue().getOriginal().getParentId())) {
                HierarchyContainer parent = eavObjectList.get(eavObject.getValue().getOriginal().getParentId());
                eavObject.getValue().setParent(parent);
                parent.addChildren(eavObject.getValue());
            }
        }
        Iterator<Map.Entry<BigInteger, HierarchyContainer>> hierarchyIterator = eavObjectList.entrySet().iterator();
        while (hierarchyIterator.hasNext()) {
            HierarchyContainer hierarchyContainer = hierarchyIterator.next().getValue();
            if (hierarchyContainer.getParent() != null) {
                hierarchyIterator.remove();
            }
        }
    }

    private void appendAttribute(AttributeObjectContainer attributeObjectContainer, EAVObject eavObject) {
        eavObject.setAttributeValue(attributeObjectContainer.getAttrId(), attributeObjectContainer.getValue());
        eavObject.setAttributeDateValue(attributeObjectContainer.getAttrId(), attributeObjectContainer.getDateValue());
        eavObject.setAttributeListValueId(attributeObjectContainer.getAttrId(), attributeObjectContainer.getListValueId());
    }
}
