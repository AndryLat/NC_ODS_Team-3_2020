package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.models.lists.LogLevel;

import java.util.Date;
import java.util.List;

public class RuleContainer {
    private String text;
    private Date dat1;
    private Date dat2;
    private List<LogLevel> levels;
    private int sort;

    public RuleContainer() {
    }

    public RuleContainer(String text, Date dat1, Date dat2, List<LogLevel> levels, int vSort) {
        this.text = text;
        this.dat1 = dat1;
        this.dat2 = dat2;
        this.levels = levels;
        this.sort = vSort;
    }

    public List<LogLevel> getLevels() {
        return levels;
    }

    public void setLevels(List<LogLevel> levels) {
        this.levels = levels;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDat1() {
        return dat1;
    }

    public void setDat1(Date dat1) {
        this.dat1 = dat1;
    }

    public Date getDat2() {
        return dat2;
    }

    public void setDat2(Date dat2) {
        this.dat2 = dat2;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
