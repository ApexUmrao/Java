package com.example.imagetranslator.controller;

import com.example.imagetranslator.service.ImageTranslationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class TranslationController {

    private final ImageTranslationService translationService;

    public TranslationController(ImageTranslationService translationService) {
        this.translationService = translationService;
    }

    @PostMapping(value = "/translate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> translateImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("targetLanguage") String targetLanguage) {

        try {
            byte[] translatedImage = translationService.translateImageText(file, targetLanguage);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("attachment", "translated.png");

            return new ResponseEntity<>(translatedImage, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

//    curl -X POST http://localhost:8080/api/images/translate \
//            -F "file=@/Users/apex/IdeaProjects/Java/image-translator/InputImages/sample.png" \
//            -F "targetLanguage=Spanish" \
//            --output /Users/apex/IdeaProjects/Java/image-translator/OutputImages/translated_image.png

}