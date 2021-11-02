package ru.avrsoft.dto;

public class SaveFile {
    private String answerBase;
    private String filePath;
    private String fullFilePath;
    private Integer taskId;
    private Integer reportId;
    private byte[] file;
    private StatusCheck statusCheck;

    public SaveFile() {
    }

    public SaveFile(String answerBase, String filePath, String fullFilePath, Integer taskId, Integer reportId, byte[] file, StatusCheck statusCheck) {
        this.answerBase = answerBase;
        this.filePath = filePath;
        this.fullFilePath = fullFilePath;
        this.taskId = taskId;
        this.reportId = reportId;
        this.file = file;
        this.statusCheck = statusCheck;
    }

    public String getAnswerBase() {
        return answerBase;
    }

    public void setAnswerBase(String answerBase) {
        this.answerBase = answerBase;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFullFilePath() {
        return fullFilePath;
    }

    public void setFullFilePath(String fullFilePath) {
        this.fullFilePath = fullFilePath;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public StatusCheck getStatusCheck() {
        return statusCheck;
    }

    public void setStatusCheck(StatusCheck statusCheck) {
        this.statusCheck = statusCheck;
    }
}
