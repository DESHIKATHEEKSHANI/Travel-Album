package edu.icet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponseDTO {
    private String id;
    private String filename;
    private String url;
    private Date uploadDate;
    private String uploadedBy;
}