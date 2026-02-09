package com.boardgameapp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/me")
public class ImageUploadController {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadImageResponse> uploadImage(
            Authentication auth,
            @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String contentType = file.getContentType();
        String ext = resolveExtension(contentType, file.getOriginalFilename());
        if (ext == null) {
            return ResponseEntity.badRequest().build();
        }
        String filename = UUID.randomUUID() + "." + ext;
        Path dir = Path.of(uploadDir).toAbsolutePath();
        Files.createDirectories(dir);
        Path target = dir.resolve(filename);
        file.transferTo(target.toFile());
        String url = "/api/uploads/" + filename;
        return ResponseEntity.ok(new UploadImageResponse(url));
    }

    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp", "bmp", "svg");

    /** Content-Type またはファイル名から拡張子を決定。画像でなければ null */
    private static String resolveExtension(String contentType, String originalFilename) {
        if (contentType != null && contentType.startsWith("image/")) {
            return extensionFromContentType(contentType);
        }
        if (originalFilename != null && originalFilename.contains(".")) {
            String ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
            if (IMAGE_EXTENSIONS.contains(ext)) {
                return "jpeg".equals(ext) ? "jpg" : ext;
            }
        }
        return null;
    }

    private static String extensionFromContentType(String contentType) {
        if (contentType == null) return "jpg";
        return switch (contentType) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            case "image/webp" -> "webp";
            case "image/bmp" -> "bmp";
            case "image/svg+xml" -> "svg";
            default -> "jpg";
        };
    }

    public record UploadImageResponse(String url) {}
}
