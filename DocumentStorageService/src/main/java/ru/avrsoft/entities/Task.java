package ru.avrsoft.entities;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @NotNull
    @Column(name = "name")
    private String name;
    @NotNull
    @Column(name = "project_id")
    private Integer projectId;
    @NotNull
    @Column(name = "worker_id")
    private Integer workerId;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;
    @NotNull
    @Column(name = "complete")
    private Integer complete;
    @Column(name = "report_id")
    private Integer reportId;
    @Column(name = "user_input_elapsed_minutes")
    private Integer userInputElapsedMinutes;
    @Column(name = "description")
    private String description;
    @Column(name = "projectid")
    private Integer projectid;
    @Column(name = "parameters")
    private String parameters;

    @OneToMany(mappedBy = "file")
    private List<File> files;

    public Task(String name, Integer projectId, Integer workerId, Date startDate, Date endDate,
                Integer complete, Integer reportId, Integer userInputElapsedMinutes,
                String description, Integer projectid, String parameters) {
        this.name = name;
        this.projectId = projectId;
        this.workerId = workerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.complete = complete;
        this.reportId = reportId;
        this.userInputElapsedMinutes = userInputElapsedMinutes;
        this.description = description;
        this.projectid = projectid;
        this.parameters = parameters;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
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

    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public Integer getUserInputElapsedMinutes() {
        return userInputElapsedMinutes;
    }

    public void setUserInputElapsedMinutes(Integer userInputElapsedMinutes) {
        this.userInputElapsedMinutes = userInputElapsedMinutes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getProjectid() {
        return projectid;
    }

    public void setProjectid(Integer projectid) {
        this.projectid = projectid;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
}
