package com.rodrickjones.imageuploader.image;

import com.rodrickjones.imageuploader.storage.UploadedImageDataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Repository
public class UploadedImageDataAccessService {
    @Autowired
    private UploadedImageDataStore uploadedImageDataStore;

    public List<UploadedImage> getUploadedImages() {
        return uploadedImageDataStore.getUploadedImages();
    }

}
