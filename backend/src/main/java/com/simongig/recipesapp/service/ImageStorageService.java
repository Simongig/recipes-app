package com.simongig.recipesapp.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * Stores recipe images in the Cloudflare R2 bucket and hands back the public URLs
 * that get persisted in {@code Recipe.imagePaths}.
 */
@Service
@Slf4j
public class ImageStorageService {

    // The stored Content-Type and file extension come from this allow-list, never from the
    // client-supplied filename, so nothing but real image types can be served from the bucket domain.
    private static final Map<String, String> ALLOWED_TYPES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp",
            "image/gif", "gif");

    private final S3Client s3Client;
    private final String bucket;
    private final String publicUrl;

    public ImageStorageService(S3Client s3Client,
                               @Value("${cloudflare.r2.bucket}") String bucket,
                               @Value("${cloudflare.r2.public-url}") String publicUrl) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.publicUrl = publicUrl.replaceAll("/+$", "");
    }

    /**
     * Uploads all non-empty images and returns their public URLs (same order).
     * All-or-nothing: if one upload fails, the ones already stored are removed again.
     */
    public List<String> upload(List<MultipartFile> images) throws IOException {
        // Browsers send an empty part when no file was picked in a file input.
        List<MultipartFile> files = images.stream().filter(image -> !image.isEmpty()).toList();

        // Validate everything first so a bad file doesn't leave the earlier ones orphaned in the bucket.
        for (MultipartFile file : files) {
            extensionFor(file);
        }

        List<String> keys = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                keys.add(put(file));
            }
        } catch (IOException | RuntimeException e) {
            keys.forEach(this::deleteQuietly);
            throw e;
        }
        return keys.stream().map(key -> publicUrl + "/" + key).toList();
    }

    private String put(MultipartFile file) throws IOException {
        String key = "recipes/" + UUID.randomUUID() + "." + extensionFor(file);
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();
        try (InputStream in = file.getInputStream()) {
            s3Client.putObject(request, RequestBody.fromInputStream(in, file.getSize()));
        }
        return key;
    }

    private String extensionFor(MultipartFile file) {
        String contentType = file.getContentType();
        String extension = contentType == null ? null : ALLOWED_TYPES.get(contentType.toLowerCase(Locale.ROOT));
        if (extension == null) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Unsupported image type: " + contentType);
        }
        return extension;
    }

    private void deleteQuietly(String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
        } catch (RuntimeException e) {
            log.warn("Could not clean up uploaded image {} after a failed upload", key, e);
        }
    }
}
