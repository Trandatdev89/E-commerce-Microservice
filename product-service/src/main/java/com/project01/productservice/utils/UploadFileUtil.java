package com.project01.productservice.utils;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UploadFileUtil {

    private final Cloudinary cloudinary;

    public String getImageToCloudinary(MultipartFile file) throws IOException {
        return cloudinary.uploader().upload(file.getBytes(), Map.of("product_id", UUID.randomUUID().toString())).get("url").toString();
    }
}
