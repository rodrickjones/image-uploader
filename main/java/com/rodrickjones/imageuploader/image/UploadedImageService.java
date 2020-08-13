package com.rodrickjones.imageuploader.image;

import com.rodrickjones.imageuploader.storage.UploadedImageDataStore;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class UploadedImageService {

    private static List<String> VALID_TYPES = Arrays.asList(ContentType.IMAGE_JPEG.getMimeType(), ContentType.IMAGE_PNG.getMimeType(),
            ContentType.IMAGE_BMP.getMimeType(), ContentType.IMAGE_GIF.getMimeType());

    @Autowired
    private UploadedImageDataAccessService uploadedImageDataAccessService;

    @Autowired
    private UploadedImageDataStore imageStore;

    public List<UploadedImage> getUploadedImages() {
        return uploadedImageDataAccessService.getUploadedImages();
    }

    public void uploadImage(String uuid, String name, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException("Empty file");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalStateException("Empty name");
        }
        if (!VALID_TYPES.contains(file.getContentType())) {
            throw new IllegalStateException("Unsupported file type: " + file.getContentType());
        }

        imageStore.save(uuid, name, file);
    }

    public byte[] downloadImage(UUID imageId) {
        UploadedImage matchedImage = uploadedImageDataAccessService.getUploadedImages()
                .stream().filter(i -> imageId.equals(i.getId())).findAny()
                .orElseThrow(() -> new IllegalStateException("No matching image found: " + imageId));
        return imageStore.getImageBytes(matchedImage);
    }
}
