package com.example.visionapi.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.protobuf.ByteString;

@RestController("/api")
public class OcrSampleController {

    @GetMapping(path="/ocr")
    public ResponseEntity<Object> ocr(){
        

        try {
            //読み込み画像を指定
            String inputImgPath = "visionapi/src/main/resources/static/image.JPG";

            Path p = Paths.get("visionapi/src/main/resources/static/Result.txt");
            System.out.println(p.toAbsolutePath());
            System.out.println(p.toString());
            // Files.createFile(p);
            //解析結果をテキストファイルで抽出
            PrintStream outputResultPath = new PrintStream(new FileOutputStream("visionapi/src/main/resources/static/Result.txt"), true);
            detectText(inputImgPath, outputResultPath);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().body("");
    }
    
    public static void detectText(String filePath, PrintStream out) throws Exception, IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
            AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
          BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
          List<AnnotateImageResponse> responses = response.getResponsesList();

          for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
              out.printf("Error: %s\n", res.getError().getMessage());
              return;
            }

            // For full list of available annotations, see http://g.co/cloud/vision/docs
            for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
              out.printf(annotation.getDescription());
              // out.printf("Text: %s\n", annotation.getDescription());
              // out.printf("Position : %s\n", annotation.getBoundingPoly());
            }
          }
        }
      }

}
