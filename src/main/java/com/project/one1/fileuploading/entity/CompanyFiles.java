package com.project.one1.fileuploading.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyFiles extends BaseEntity{

    @Basic
    @Column(name = "file_name")
    private String fileName;

    @Basic
    @Column(name = "file_location")
    private String fileLocation;

    @Basic
    @Column(name = "meta_data")
    private String metaData;

    public CompanyFiles(long id, String fileName, String metaData, String fileLocation, LocalDateTime createdOn) {
        this.setId(id);
        this.fileName = fileName;
        this.metaData = metaData;
        this.fileLocation = fileLocation;
        this.createdOn = createdOn;
    }

//    @Basic
//    @Column(name = "company_id")
//    private int companyId;
//
//    public int getCompanyId() {
//        return companyId;
//    }
//
//    public void setCompanyId(int companyId) {
//        this.companyId = companyId;
//    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }
}
