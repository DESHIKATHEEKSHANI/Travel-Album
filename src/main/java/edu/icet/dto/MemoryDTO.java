package edu.icet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoryDTO {
    private String id;
    private String location;
    private Date date;
    private String description;
    private String imageUrl;
    private String username;
    private String category;  // Added field
    private boolean favorite; // Added field
    private Date createdAt;
}