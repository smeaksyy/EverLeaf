package com.everleaf.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FileUtil {
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "docx", "txt", "png", "jpg", "jpeg");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String UPLOAD_DIR = System.getProperty("user.home") + File.separator + "everleaf_uploads";

    public static String generateStorageFilename(String originalFilename) {
        return UUID.randomUUID().toString() + "." + getFileExtension(originalFilename);
    }

    public static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    public static boolean isAllowedFileType(String mimeType) {
        if (mimeType == null) return true;
        String m = mimeType.toLowerCase();
        return m.contains("pdf") ||
               m.contains("word") ||
               m.contains("officedocument") ||
               m.contains("text") ||
               m.contains("image") ||
               m.contains("octet-stream");
    }

    public static boolean isAllowedExtension(String filename) {
        String ext = getFileExtension(filename);
        return ext.isEmpty() || ALLOWED_EXTENSIONS.contains(ext) || ext.equals("doc");
    }

    public static boolean isAllowedFileSize(long size) {
        return size > 0 && size <= MAX_FILE_SIZE;
    }

    public static String getUploadDirectory() {
        return UPLOAD_DIR;
    }

    public static void ensureUploadDirectoryExists() {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
