package com.Realty.RealtyWeb.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {
    private final String uploadPath = "C:/realty/upload/images/";


    @Override
    public String save(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadPath, filename);
            Files.copy(file.getInputStream(), path);
            return "/images/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패", e);
        }
    }
}
