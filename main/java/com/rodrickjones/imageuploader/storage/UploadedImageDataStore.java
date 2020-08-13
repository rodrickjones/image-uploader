package com.rodrickjones.imageuploader.storage;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import com.rodrickjones.imageuploader.image.UploadedImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Repository
public class UploadedImageDataStore {

    @Autowired
    private AmazonS3 s3;

    @Autowired
    @Value("${aws.bucket.name}")
    private String awsBucketName;

    public List<UploadedImage> getUploadedImages() {
        List<UploadedImage> images = new ArrayList<>();
        ObjectListing objectListing = s3.listObjects(awsBucketName);
        for (S3ObjectSummary summary : objectListing.getObjectSummaries()) {
            try (S3Object object = s3.getObject(summary.getBucketName(), summary.getKey())) {
                Map<String, String> metadata = object.getObjectMetadata().getUserMetadata();
                String uuid = metadata.get("UUID");
                String name = metadata.get("Name");
                String uploadedTimestamp = metadata.get("Uploaded-Timestamp");
                if (uuid != null && name != null && uploadedTimestamp != null) {
                    UploadedImage image = new UploadedImage(UUID.fromString(uuid), name, summary.getKey(), Long.parseLong(uploadedTimestamp));
                    images.add(image);
                } else {
                    System.out.println("Object missing required metadata");
                    s3.deleteObject(awsBucketName, summary.getKey());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        images.sort(Comparator.comparing(UploadedImage::getUploadedTimestamp).reversed());
        return images;
    }

    public void save(String uuid, String name,
                     MultipartFile file) {
        ObjectMetadata optionalMetadata = new ObjectMetadata();

        optionalMetadata.addUserMetadata("UUID", uuid);
        optionalMetadata.addUserMetadata("Name", name);
        optionalMetadata.addUserMetadata("Uploaded-Timestamp", String.valueOf(System.currentTimeMillis()));

        optionalMetadata.addUserMetadata("Content-Type", file.getContentType());
        optionalMetadata.addUserMetadata("Content-Length", String.valueOf(file.getSize()));
        optionalMetadata.addUserMetadata("Original-File-Name", file.getOriginalFilename());

        try {
            s3.putObject(awsBucketName, String.format("%s-%s", uuid, file.getOriginalFilename()), file.getInputStream(), optionalMetadata);
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("Failed to save content to s3", e);
        }
    }

    public byte[] getImageBytes(UploadedImage image) {
        try (S3Object object = s3.getObject(awsBucketName, image.getLink())) {
            return IOUtils.toByteArray(object.getObjectContent());
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("Failed to download file from s3", e);
        }
    }
}
