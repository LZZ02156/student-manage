package com.rollcall.controller;

import com.rollcall.controller.UploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/uploads")
public class UploadController {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.max-size:5242880}")
    private long maxSize; // default 5MB

    @Value("${app.upload.allowed-types:image/jpeg,image/png}")
    private String allowedTypes;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("file missing");
        }
        if (file.getSize() > maxSize) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("file too large");
        }
        String contentType = file.getContentType();
        boolean okType = false;
        for (String t : allowedTypes.split(",")) {
            if (t.trim().equalsIgnoreCase(contentType)) { okType = true; break; }
        }
        if (!okType) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("unsupported media type");
        }

        try {
            String original = StringUtils.cleanPath(file.getOriginalFilename());
            String ext = "";
            int i = original.lastIndexOf('.');
            if (i >= 0) ext = original.substring(i);
            String filename = UUID.randomUUID().toString() + ext;
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path target = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), target);

            // generate thumbnail for images
            String thumbFilename = null;
            try {
                BufferedImage img = ImageIO.read(target.toFile());
                if (img != null) {
                    int thumbW = 300;
                    int thumbH = (int) ((double) thumbW / img.getWidth() * img.getHeight());
                    Image tmp = img.getScaledInstance(thumbW, thumbH, Image.SCALE_SMOOTH);
                    BufferedImage thumb = new BufferedImage(thumbW, thumbH, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2d = thumb.createGraphics();
                    g2d.drawImage(tmp, 0, 0, null);
                    g2d.dispose();

                    String thumbDir = uploadDir + File.separator + "thumbs";
                    Path thumbPath = Paths.get(thumbDir);
                    if (!Files.exists(thumbPath)) Files.createDirectories(thumbPath);
                    thumbFilename = "thumb_" + filename;
                    File out = thumbPath.resolve(thumbFilename).toFile();
                    ImageIO.write(thumb, "jpg", out);
                }
            } catch (Exception ex) {
                // ignore thumbnail failures
            }

            // return accessible url path(s)
            String url = "/uploads/" + filename;
            String thumbUrl = (thumbFilename != null) ? "/uploads/thumbs/" + thumbFilename : null;
            UploadResponse resp = new UploadResponse(url, thumbUrl);
            return ResponseEntity.ok(resp);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
        }
    }
}
