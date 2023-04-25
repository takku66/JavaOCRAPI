package com.ocrapi.tesseract.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@RestController
@RequestMapping("/api")
public class ImageController {

    @PostMapping("/image")
    public ResponseEntity<Object> getString(@RequestBody(required = true) String file) throws IOException {

        Path relativePath = Paths.get("");
        Path absolutePath = relativePath.toAbsolutePath();

        System.out.println(absolutePath.toString());

        // Base64からのデコード
        byte[] bytes = Base64.getDecoder().decode(file.split(",")[1]);

        Path imagePath = Paths.get("./src/main/resources/static/image.png");
        System.out.println(Files.exists(imagePath));
        File imageFile = imagePath.toFile();
        if( !Files.exists(imagePath)){
            imageFile.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(imageFile);
        fos.write(bytes);
        fos.close();

        String computedStr = ocr(imageFile);

        return ResponseEntity.ok().body(computedStr);
    }

    private String ocr(File file){

        ITesseract instance = new Tesseract();

        try {
            long start = System.currentTimeMillis();
            instance.setLanguage("jpn");
            String result = instance.doOCR(file);
            
            long end = System.currentTimeMillis();
            System.out.println(result);
            
            System.out.println((end - start) / 1000  + "[sec]");
            return result;
        } catch (TesseractException ex) {
            ex.printStackTrace();
        }
        return "";
    }

}
