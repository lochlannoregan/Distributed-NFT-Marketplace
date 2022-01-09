package service.image;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
  public static void main(String[] args) throws IOException {

    // initiialise firebase app
    ClassLoader classLoader = Application.class.getClassLoader();
    InputStream serviceAccount = Objects.requireNonNull(classLoader.getResourceAsStream("firebase_key.json"));
    FirebaseOptions options = FirebaseOptions.builder()
      .setCredentials(GoogleCredentials.fromStream(serviceAccount))
      .setStorageBucket("joloto-bdbe6.appspot.com")
      .build();
    FirebaseApp.initializeApp(options);

    SpringApplication.run(Application.class, args);
  }

}