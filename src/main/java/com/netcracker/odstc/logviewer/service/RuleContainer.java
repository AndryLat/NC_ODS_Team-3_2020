package com.netcracker.odstc.logviewer.service;

import java.util.Date;

public class RuleContainer {
    private final String text;
    private final Date dat1;
    private final Date dat2;
    private final int vSevere;
    private final int vWarning;
    private final int vInfo;
    private final int vConfig;
    private final int vFine;
    private final int vFiner;
    private final int vFinest;
    private final int vDebug;
    private final int vTrace;
    private final int vError;
    private final int vFatal;
    private final int vSort;

    public RuleContainer(String text, Date dat1, Date dat2, int vSevere, int vWarning, int vInfo, int vConfig, int vFine, int vFiner, int vFinest, int vDebug, int vTrace, int vError, int vFatal, int vSort) {
        this.text = text;
        this.dat1 = dat1;
        this.dat2 = dat2;
        this.vSevere = vSevere;
        this.vWarning = vWarning;
        this.vInfo = vInfo;
        this.vConfig = vConfig;
        this.vFine = vFine;
        this.vFiner = vFiner;
        this.vFinest = vFinest;
        this.vDebug = vDebug;
        this.vTrace = vTrace;
        this.vError = vError;
        this.vFatal = vFatal;
        this.vSort = vSort;
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
        return vSevere;
    }

    public int getWarning() {
        return vWarning;
    }

    public int getInfo() {
        return vInfo;
    }

    public int getConfig() {
        return vConfig;
    }

    public int getFine() {
        return vFine;
    }

    public int getFiner() {
        return vFiner;
    }

    public int getFinest() {
        return vFinest;
    }

    public int getDebug() {
        return vDebug;
    }

    public int getTrace() {
        return vTrace;
    }

    public int getError() {
        return vError;
    }

    public int getFatal() {
        return vFatal;
    }

    public int getSort() {
        return vSort;
    }
}
