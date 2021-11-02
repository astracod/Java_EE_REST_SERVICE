package ru.avrsoft.dto;


import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;


public class FileResponse {

    private Integer id;

    private String fileName;

    private String filePath;

    private String sha5;

    private Integer reportId;

    private Integer taskId;

    public FileResponse() {
    }

    public FileResponse(Integer id, String fileName, String filePath, String sha5, Integer reportId, Integer taskId) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.sha5 = sha5;
        this.reportId = reportId;
        this.taskId = taskId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSha5() {
        return sha5;
    }

    public void setSha5(String sha5) {
        this.sha5 = sha5;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

}
