package com.netcracker.odstc.logviewer.containers.dto;

import java.math.BigInteger;
import java.util.Date;

public class LogDTO {
    private BigInteger objectId;
    private BigInteger level;
    private String text;
    private Date creationDate;

    public BigInteger getObjectId() {
        return objectId;
    }

    public void setObjectId(BigInteger objectId) {
        this.objectId = objectId;
    }

    public BigInteger getLevel() {
        return level;
    }

    public void setLevel(BigInteger level) {
        this.level = level;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
