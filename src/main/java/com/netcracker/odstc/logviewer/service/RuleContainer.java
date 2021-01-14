package com.netcracker.odstc.logviewer.service;

import java.util.Date;

public class RuleContainer {
    private String text;
    private Date dat1;
    private Date dat2;
    private int severe;
    private int warning;
    private int info;
    private int config;
    private int fine;
    private int finer;
    private int finest;
    private int debug;
    private int trace;
    private int error;
    private int fatal;
    private int sort;

    public RuleContainer(){
    }

    public RuleContainer(String text, Date dat1, Date dat2, int severe, int vWarning, int vInfo, int vConfig, int vFine, int vFiner, int vFinest, int vDebug, int vTrace, int vError, int vFatal, int vSort) {
        this.text = text;
        this.dat1 = dat1;
        this.dat2 = dat2;
        this.severe = severe;
        this.warning = vWarning;
        this.info = vInfo;
        this.config = vConfig;
        this.fine = vFine;
        this.finer = vFiner;
        this.finest = vFinest;
        this.debug = vDebug;
        this.trace = vTrace;
        this.error = vError;
        this.fatal = vFatal;
        this.sort = vSort;
    }

    public String getText() {
        return text;
    }

    public Date getDat1() {
        return dat1;
    }

    public Date getDat2() {
        return dat2;
    }

    public int getSevere() {
        return severe;
    }

    public int getWarning() {
        return warning;
    }

    public int getInfo() {
        return info;
    }

    public int getConfig() {
        return config;
    }

    public int getFine() {
        return fine;
    }

    public int getFiner() {
        return finer;
    }

    public int getFinest() {
        return finest;
    }

    public int getDebug() {
        return debug;
    }

    public int getTrace() {
        return trace;
    }

    public int getError() {
        return error;
    }

    public int getFatal() {
        return fatal;
    }

    public int getSort() {
        return sort;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDat1(Date dat1) {
        this.dat1 = dat1;
    }

    public void setDat2(Date dat2) {
        this.dat2 = dat2;
    }

    public void setSevere(int severe) {
        this.severe = severe;
    }

    public void setWarning(int warning) {
        this.warning = warning;
    }

    public void setInfo(int info) {
        this.info = info;
    }

    public void setConfig(int config) {
        this.config = config;
    }

    public void setFine(int fine) {
        this.fine = fine;
    }

    public void setFiner(int finer) {
        this.finer = finer;
    }

    public void setFinest(int finest) {
        this.finest = finest;
    }

    public void setDebug(int debug) {
        this.debug = debug;
    }

    public void setTrace(int trace) {
        this.trace = trace;
    }

    public void setError(int error) {
        this.error = error;
    }

    public void setFatal(int fatal) {
        this.fatal = fatal;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
