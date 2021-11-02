package ru.avrsoft.dto;

import javax.persistence.Entity;
import java.util.LinkedHashSet;
import java.util.List;

@Entity
public class AllFilesResponse {

    private List<FileResponse> files;
    private LinkedHashSet<FileResponse> uniqueList;


    public AllFilesResponse() {
    }

    public AllFilesResponse(List<FileResponse> files) {
        this.files = files;
    }

    public List<FileResponse> getFiles() {
        return files;
    }

    public void setFiles(List<FileResponse> files) {
        this.files = files;
    }

    public LinkedHashSet<FileResponse> getUniqueList() {
        return uniqueList;
    }

    public void setUniqueList(LinkedHashSet<FileResponse> uniqueList) {
        this.uniqueList = uniqueList;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
