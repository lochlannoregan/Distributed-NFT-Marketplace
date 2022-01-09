package service.image;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import core.Image;

@Service
public class ImageService {

  Firestore db = FirestoreClient.getFirestore();
  Bucket bucket = StorageClient.getInstance().bucket();

  public ResponseEntity<String> addImage(String title, MultipartFile image) throws InterruptedException, ExecutionException, IOException {
    // create new document in firestore
    DocumentReference docRef = db.collection("images").document();

    // add image to bucket, with same id as document
    String filename = image.getOriginalFilename();
    String extension = "." + filename.substring(filename.lastIndexOf(".") + 1);
    Blob blob = bucket.create(docRef.getId().toString() + extension, image.getBytes(), "image/*");
    
    // add image reference (title and storage location) to document
    Map<String, Object> data = new HashMap<>();
    data.put("title", title);
    data.put("extension", extension);
    data.put("size", blob.getSize());
    // data.put("location", blob.getMediaLink());
    ApiFuture<WriteResult> result = docRef.set(data);
    System.out.println("Image added succesfully at " + result.get().getUpdateTime().toString() + ". id: " + docRef.getId().toString());

    return ResponseEntity.ok(docRef.getId().toString());
  }

  public ResponseEntity<Image> getImage(String id) throws InterruptedException, ExecutionException {
    // get firestore document
    DocumentReference docRef = db.collection("images").document(id);
    ApiFuture<DocumentSnapshot> future = docRef.get();
    DocumentSnapshot document = future.get();

    // if document exists, return image class
    if(document.exists()) {
      // get image blob from storage
      Blob blob = bucket.get(id + document.getString("extension"));
      // crerate signed URL with longevity of 1 day
      String url = blob.signUrl(1, TimeUnit.DAYS).toString();
      // create new image object
      Image image = new Image(id, document.getString("title"), url);
      System.out.println("Returning image with id: " + id);
      return ResponseEntity.ok(image);
    }
    System.out.println("Could not find image with id: " + id);
    // else return 404
    return new ResponseEntity<Image>(HttpStatus.NOT_FOUND);
  }

  public ResponseEntity<String> removeImage(String id) throws InterruptedException, ExecutionException {
    // get firestore document
    DocumentReference docRef = db.collection("images").document(id);
    ApiFuture<DocumentSnapshot> future = docRef.get();
    DocumentSnapshot document = future.get();

    // if document exists, remove and return ok response
    if(document.exists()) {
      // remove image from storage bucket
      Blob blob = bucket.get(id + document.getString("extension"));
      blob.delete();

      // remove document
      ApiFuture<WriteResult> result =  db.collection("images").document(id).delete();
      System.out.println("Deleted image with id: " + id);
      return ResponseEntity.ok("Successfully deleted " + id + " at " + result.get().getUpdateTime().toString());
    }
    System.out.println("Could not find image with id: " + id);
    // else return 404
    return new ResponseEntity<String>("Could not find image with id: " + id, HttpStatus.NOT_FOUND);
  }
}
