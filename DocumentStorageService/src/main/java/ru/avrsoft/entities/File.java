package ru.avrsoft.entities;

import javax.persistence.*;

@Entity(name = "file")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "file_path")
    private String filePath;
    @Column(name = "sha5")
    private String sha5;
    @Column(name = "report_id")
    private Integer reportId;
    @Column(name = "task_id")
    private Integer taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;


    public File() {
    }

    public File(String fileName, String filePath, String sha5, Integer reportId, Integer taskId, Task task) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.sha5 = sha5;
        this.reportId = reportId;
        this.taskId = taskId;
        this.task = task;
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

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) return false;
        if (this == obj) return true;
        if(this.getClass() != obj.getClass()) return false;
        File otherObj = (File) obj;
        return this.id == otherObj.id;
    }
}
