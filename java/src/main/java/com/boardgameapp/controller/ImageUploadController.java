package com.boardgameapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 認証ユーザー向けの画像アップロードAPIを提供するコントローラ。
 */
@RestController
@RequestMapping("/api/me")
public class ImageUploadController {

    private static final Logger log = LoggerFactory.getLogger(ImageUploadController.class);

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /**
     * 画像ファイルをアップロードし、公開URLを返す。保存先は認証ユーザーごとのサブディレクトリ。
     *
     * @param auth 認証情報（ユーザーごとの保存パスに利用）
     * @param file アップロードする画像ファイル
     * @return アクセス用URL（/api/uploads/xxx）またはエラー時は 500 とメッセージ
     */
    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(
            Authentication auth,
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String contentType = file.getContentType();
        String ext = resolveExtension(contentType, file.getOriginalFilename());
        if (ext == null) {
            return ResponseEntity.badRequest().build();
        }
        String filename = UUID.randomUUID() + "." + ext;
        String safeUser = auth.getName().replaceAll("[^a-zA-Z0-9_-]", "_");
        Path dir = Path.of(uploadDir).toAbsolutePath().resolve(safeUser);
        try {
            Files.createDirectories(dir);
            Path target = dir.resolve(filename);
            file.transferTo(target.toFile());
        } catch (IOException e) {
            log.error("Failed to save uploaded image: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to save image. Please try again."));
        }
        String url = "/api/uploads/" + safeUser + "/" + filename;
        return ResponseEntity.ok(new UploadImageResponse(url));
    }

    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp", "bmp");

    /** Content-Type またはファイル名から拡張子を決定。画像でなければ null。SVG は許可しない。 */
    private static String resolveExtension(String contentType, String originalFilename) {
        if ("image/svg+xml".equals(contentType)) {
            return null;
        }
        if (contentType != null && contentType.startsWith("image/")) {
            String ext = extensionFromContentType(contentType);
            if (ext != null) return ext;
        }
        if (originalFilename != null && originalFilename.contains(".")) {
            String ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
            if ("svg".equals(ext)) return null;
            if (IMAGE_EXTENSIONS.contains(ext)) {
                return "jpeg".equals(ext) ? "jpg" : ext;
            }
        }
        return null;
    }

    /**
     * Content-Type から保存用拡張子を返す。ホワイトリスト外または null の場合は null。SVG は含めない。
     *
     * @param contentType 例: image/png
     * @return 拡張子（jpg, png など）。未対応の場合は null
     */
    private static String extensionFromContentType(String contentType) {
        if (contentType == null) return null;
        return switch (contentType) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            case "image/webp" -> "webp";
            case "image/bmp" -> "bmp";
            default -> null;
        };
    }

    /**
     * アップロード結果。画像へのアクセスURLを保持する。
     *
     * @param url 例: /api/uploads/xxx.jpg
     */
    public record UploadImageResponse(String url) {}
}
