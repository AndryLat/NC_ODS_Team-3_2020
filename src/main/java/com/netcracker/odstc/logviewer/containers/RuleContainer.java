package com.netcracker.odstc.logviewer.containers;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.netcracker.odstc.logviewer.models.lists.LogLevel;

import java.util.Date;
import java.util.List;

public class RuleContainer {
    @JsonAlias("text")
    private String searchText;
    @JsonAlias("dat1")
    private Date startDate;
    @JsonAlias("dat2")
    private Date endDate;
    private List<LogLevel> levels;
    @JsonAlias("sort")
    private SortType sortType;

    public RuleContainer() {
    }

    public RuleContainer(String searchText, Date startDate, Date endDate, List<LogLevel> levels, SortType sortType) {
        this.searchText = searchText;
        this.startDate = startDate;
        this.endDate = endDate;
        this.levels = levels;
        this.sortType = sortType;
    }

    public List<LogLevel> getLevels() {
        return levels;
    }

    public void setLevels(List<LogLevel> levels) {
        this.levels = levels;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public SortType getSortType() {
        return sortType;
    }

    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }
}
