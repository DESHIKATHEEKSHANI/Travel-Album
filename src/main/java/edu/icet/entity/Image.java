package edu.icet.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "images") // MongoDB collection name
public class Image {

    @Id
    private String id;
    private String filename;
    private String url;
    private Date uploadDate;
    private String uploadedBy;

    public Image(String filename, String url, Date uploadDate, String uploadedBy) {
        this.filename = filename;
        this.url = url;
        this.uploadDate = uploadDate;
        this.uploadedBy = uploadedBy;
    }

    public String getId() { return id; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Date getUploadDate() { return uploadDate; }
    public void setUploadDate(Date uploadDate) { this.uploadDate = uploadDate; }
    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
}

