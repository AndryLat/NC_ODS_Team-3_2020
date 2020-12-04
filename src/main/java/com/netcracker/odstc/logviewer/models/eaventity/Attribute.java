package com.netcracker.odstc.logviewer.models.eaventity;

import java.math.BigInteger;
import java.util.Date;

/**
 * Description:
 *
 * @author Aleksanid
 * created 03.12.2020
 */
public class Attribute {
    private String value;
    private Date dateValue;
    private BigInteger listValueId;

    public Attribute(BigInteger listValueId) {
        this.listValueId = listValueId;
    }

    public Attribute(Date dateValue) {
        this.dateValue = dateValue;
    }

    public Attribute(String value) {
        this.value = value;
    }

    public Attribute() {
    }

    public Attribute(String value, Date dateValue, BigInteger listValueId) {
        this.value = value;
        this.dateValue = dateValue;
        this.listValueId = listValueId;
    }

    @Override
    public String toString() {
        return "Attribute{" +
                "value='" + value + '\'' +
                ", dateValue=" + dateValue +
                ", listValueId=" + listValueId +
                '}';
    }

    public Attribute(String value, java.sql.Date date_value, BigInteger list_value_id) {
        this.value = value;
        this.dateValue = date_value;
        this.listValueId = list_value_id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public BigInteger getListValueId() {
        return listValueId;
    }

    public void setListValueId(BigInteger listValueId) {
        this.listValueId = listValueId;
    }
}
