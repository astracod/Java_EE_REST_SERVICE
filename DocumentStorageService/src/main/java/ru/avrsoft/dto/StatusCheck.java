package ru.avrsoft.dto;

public class StatusCheck {
    private  Integer resultQuery;
    private  String statusCheck;

    public StatusCheck() {
    }

    public StatusCheck(Integer resultQuery, String statusCheck) {
        this.resultQuery = resultQuery;
        this.statusCheck = statusCheck;
    }

    public Integer getResultQuery() {
        return resultQuery;
    }

    public void setResultQuery(Integer resultQuery) {
        this.resultQuery = resultQuery;
    }

    public String getStatusCheck() {
        return statusCheck;
    }

    public void setStatusCheck(String statusCheck) {
        this.statusCheck = statusCheck;
    }
}
