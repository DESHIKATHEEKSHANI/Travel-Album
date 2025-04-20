package edu.icet.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "memories")
public class Memory {
    @Id
    private String id;
    private String location;
    private Date date;
    private String description;
    private String imageUrl;
    private String username;
    private Date createdAt;
}