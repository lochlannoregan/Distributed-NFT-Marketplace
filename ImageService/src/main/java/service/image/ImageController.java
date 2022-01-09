package service.image;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RestController;

import core.Image;

@RestController
public class ImageController {

  @Autowired
  private ImageService imageService;

  @PostMapping("/add-image")
  public ResponseEntity<String> addImage(@RequestParam("title") String title, @RequestParam("image") MultipartFile image) throws InterruptedException, ExecutionException, IOException {
    return imageService.addImage(title, image);
  }

  @GetMapping("/get-image")
  public ResponseEntity<Image> getImage(@RequestParam String id) throws InterruptedException, ExecutionException {
    return imageService.getImage(id);
  }

  @PutMapping("/remove-image")
  public ResponseEntity<String> removeImage(@RequestParam String id) throws InterruptedException, ExecutionException {
    return imageService.removeImage(id);
  }

}
