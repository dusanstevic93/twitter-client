package com.dusan.twitterclient.api;

import com.dusan.twitterclient.service.MediaService;
import com.dusan.twitterclient.service.model.MediaResource;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@RestController
@RequestMapping("/api/media")
@AllArgsConstructor
public class MediaController {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/png");
    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of("video/mp4");

    private final MediaService mediaService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public MediaResource uploadMedia(@RequestParam("username") String username, @RequestParam("media") MultipartFile file) {
        if (file.isEmpty())
            throw new MediaUploadException("File must not be empty");

        String contentType = file.getContentType();
        if (ALLOWED_IMAGE_TYPES.contains(contentType)) {
            validateImage(file);
            return uploadImage(username, file);
        }
        else if (ALLOWED_VIDEO_TYPES.contains(contentType)) {
            validateVideo(file);
            return uploadVideo(username, file);
        }
        else {
            throw new MediaUploadException("Unsupported content type");
        }
    }

    private void validateImage(MultipartFile file) {
        final long maxImageSize = 5000000; // 5 mb
        if (file.getSize() > maxImageSize) {
            throw new MediaUploadException("Image must be smaller than 5 mb");
        }
    }

    private MediaResource uploadImage(String username, MultipartFile image) {
        try (InputStream inputStream = image.getInputStream()) {
            return mediaService.uploadImage(username, image.getName(), inputStream);
        } catch (IOException ex) {
            throw new RuntimeException();
        }
    }

    private void validateVideo(MultipartFile file) {
        // da se zavrsi
    }

    private MediaResource uploadVideo(String username, MultipartFile video) {
        try (InputStream inputStream = video.getInputStream()) {
            return mediaService.uploadImage(username, video.getName(), inputStream);
        } catch (IOException ex) {
            throw new RuntimeException();
        }
    }
}
