package com.mhealth.admin.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "mh_image_gallery")
public class ImageGallery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Name cannot be blank") // Ensures the field is not empty or null
    @Size(max = 255, message = "File name cannot exceed 255 characters") // Limits the maximum length of the file name
    private String name;
}
