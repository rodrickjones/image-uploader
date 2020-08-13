package com.rodrickjones.imageuploader.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/uploaded-image")
@CrossOrigin("*")
public class UploadedImageController {

    @Autowired
    private UploadedImageService uploadedImageService;

    @GetMapping
    public List<UploadedImage> getUploadedImages() {
        return uploadedImageService.getUploadedImages();
    }

    @PostMapping (
            path = "upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void uploadImage(@RequestParam String name,
            @RequestParam MultipartFile file) {
        uploadedImageService.uploadImage(UUID.randomUUID().toString(), name, file);
    }

    @GetMapping (
            path = "{imageId}"
    )
    public byte[] downloadImage(@PathVariable("imageId") UUID uploadedImageId) {
        return uploadedImageService.downloadImage(uploadedImageId);
    }
}
