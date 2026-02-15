package com.Realty.RealtyWeb.services;


import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    public String save(MultipartFile file);
}
