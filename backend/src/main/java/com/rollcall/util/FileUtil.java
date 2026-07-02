package com.rollcall.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileUtil {
    public static String generateFileName(String original) {
        String ext = "jpg";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.') + 1);
        }
        return UUID.randomUUID().toString() + "." + ext;
    }

    public static void saveFile(MultipartFile file, String uploadPath, String fileName) throws IOException {
        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();
        File target = new File(dir, fileName);
        file.transferTo(target);
    }
}
