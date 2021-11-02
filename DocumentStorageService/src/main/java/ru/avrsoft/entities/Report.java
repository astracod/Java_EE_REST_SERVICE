package ru.avrsoft.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity(name = "report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @NotNull
    @Column(name = "date")
    private Date date;
    @Column(name = "name")
    private String name;
    @Column(name = "file_path")
    private String filePath;
    @NotNull
    @Column(name = "version_code")
    private String versionCode;

    @OneToMany(mappedBy = "file")
    private List<File> files;

    public Report(Date date, String name, String filePath, String versionCode) {
        this.date = date;
        this.name = name;
        this.filePath = filePath;
        this.versionCode = versionCode;
    }

    public Integer getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
}
