package com.rodrickjones.imageuploader.image;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class UploadedImage {
    private UUID id;
    private String name;
    private String link; // S3 key
    private long uploadedTimestamp;

}
