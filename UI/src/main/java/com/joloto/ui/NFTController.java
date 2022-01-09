package com.joloto.ui;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import core.Image;
import core.Sale;

@Controller
public class NFTController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    String index(Model model) {
      // get authentication data
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();

      // check if logged in
      Boolean loggedIn = false;
      String role = auth.getAuthorities().toString();
      if (role.equals("[ROLE_NFTUSER]")) {loggedIn = true;};
      model.addAttribute("loggedIn", loggedIn);

      // get username
      String name = auth.getName();
      model.addAttribute("username", name);

      return "index";
    }

    @GetMapping("/signup")
    String signup(Model model) {
      // get authentication data
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();

      // check if logged in
      Boolean loggedIn = false;
      String role = auth.getAuthorities().toString();
      if (role.equals("[ROLE_NFTUSER]")) {loggedIn = true;};
      model.addAttribute("loggedIn", loggedIn);

      // get username
      String name = auth.getName();
      model.addAttribute("username", name);
        return "signup";
    }

    @PostMapping("/signup")
    String createAccount(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password) {

        String uri = "http://accountservice-service:8081/signup";

        System.out.println("FROM THE SIGN UP " + username);

        uri += "?username=";
        uri = uri + username;
        uri += "&password=" + password;

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.postForObject(uri, null, String.class);

        System.out.println(response);

        return "login";

    }

    @GetMapping("/login")
    String login(Model model) {
      // get authentication data
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();

      // check if logged in
      Boolean loggedIn = false;
      String role = auth.getAuthorities().toString();
      if (role.equals("[ROLE_NFTUSER]")) {loggedIn = true;};
      model.addAttribute("loggedIn", loggedIn);

      // get username
      String name = auth.getName();
      model.addAttribute("username", name);
      return "login";
    }

    @GetMapping("/upload")
    String upload(Model model) {
      // get authentication data
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();

      // check if logged in
      Boolean loggedIn = false;
      String role = auth.getAuthorities().toString();
      if (role.equals("[ROLE_NFTUSER]")) {loggedIn = true;};
      model.addAttribute("loggedIn", loggedIn);

      // get username
      String name = auth.getName();
      model.addAttribute("username", name);
      return "upload";
    }

    @PostMapping("/upload")
    ModelAndView uploadImage(@RequestParam("title") String title, @RequestParam("image") MultipartFile image, Model model) throws IOException {

      // get authentication data
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();

      // check if logged in
      Boolean loggedIn = false;
      String role = auth.getAuthorities().toString();
      if (role.equals("[ROLE_NFTUSER]")) {loggedIn = true;};
      model.addAttribute("loggedIn", loggedIn);

      // get username
      String name = auth.getName();
      model.addAttribute("username", name);

      // upload image using ImageService
      String suffix = null;
      String contentType = image.getContentType();
      if (contentType.split("/")[0].equals("image")) {
        suffix = "." + contentType.split("/")[1];
      }
      Path tempFile = Files.createTempFile(null, suffix);
      Files.write(tempFile, image.getBytes());
      File fileToSend = tempFile.toFile();
       fileToSend.deleteOnExit();
      RestTemplate restTemplate = new RestTemplate();
      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://imageservice-service:8083/add-image");
      builder.queryParam("title", title);
      LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
      params.add("image", new FileSystemResource(fileToSend));
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);
      HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);
      ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, requestEntity, String.class);
      HttpStatus statusCode = responseEntity.getStatusCode();

      // if image uploaded successfully, add ID to user collection
      if (statusCode == HttpStatus.OK) {
        String id = responseEntity.getBody();
        restTemplate.postForObject("http://accountservice-service:8081/nft-added?username=" + name + "&imageID=" + id, null, String.class);
      }

      return new ModelAndView("redirect:/collection");
    }

    @GetMapping("/collection")
    String myCollection(Model model) {
      // get authentication data
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();

      // check if logged in
      Boolean loggedIn = false;
      String role = auth.getAuthorities().toString();
      if (role.equals("[ROLE_NFTUSER]")) {loggedIn = true;};
      model.addAttribute("loggedIn", loggedIn);

      // get username
      String name = auth.getName();
      model.addAttribute("username", name);

      // get array of all NFT Ids in user collection
      String uriIDs = "http://accountservice-service:8081/get-nfts?username=" + auth.getName();
      RestTemplate getNFTIDs = new RestTemplate();
      @SuppressWarnings("unchecked")
      ArrayList<String> response = getNFTIDs.getForObject(uriIDs, ArrayList.class);

      // for each Id in collection, get image and add to map
      Map<String, Image> nftList = new HashMap<>();
      for (String id : response) {
          String uriImages = "http://imageservice-service:8083/get-image?id=" + id;
          RestTemplate getImage = new RestTemplate();
          Image image = getImage.getForObject(uriImages, Image.class);
          nftList.put(id, image);
      }

      model.addAttribute("nftList", nftList);

      return "collection";
    }

    @GetMapping("/account")
    String myAccount(@RequestParam(required = false) String sell, Model model) {
      // get authentication data
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();

      // check if logged in
      Boolean loggedIn = false;
      String role = auth.getAuthorities().toString();
      if (role.equals("[ROLE_NFTUSER]")) {loggedIn = true;};
      model.addAttribute("loggedIn", loggedIn);

      // get username
      String name = auth.getName();
      model.addAttribute("username", name);

      // if id given to sell parameter, attempt to sell and return result
      if (sell != null) {
        // get sale from marketplace service
        String uriSale = "http://marketplaceservice-service:8082/sell";
        RestTemplate getSale = new RestTemplate();
        Sale responseSale = getSale.getForObject(uriSale, Sale.class);

        // remove NFT from account
        BigDecimal saleAmount = responseSale.getValue();
        String uriRemove = "http://accountservice-service:8081/nft-sold?username=" + name + "&imageID=" + sell + "&amount=" + saleAmount;
        RestTemplate getRemove = new RestTemplate();
        String responseRemove = getRemove.postForObject(uriRemove, null, String.class);

        // remove NFT from image service
        String uriRemoveImage = "http://imageservice-service:8083/remove-image?id=" + sell;
        RestTemplate getRemoveImage = new RestTemplate();
        getRemoveImage.put(uriRemoveImage, null);

        // create responses for user
        if (responseRemove.equals("OK")) {
          model.addAttribute("sell", sell);
          model.addAttribute("saleAmount", saleAmount);
          model.addAttribute("saleMessage", responseSale.getMessage());
        } else {
          model.addAttribute("sell", "error");
        }
      }

      // account balance (get after sell)
      String uriBal = "http://accountservice-service:8081/get-balance?username=" + name;
      RestTemplate getBal = new RestTemplate();
      BigDecimal responseBal = getBal.getForObject(uriBal, BigDecimal.class);
      model.addAttribute("balance", responseBal);

      // number of NFTs (get after sell)
      String uriIDs = "http://accountservice-service:8081/get-nfts?username=" + name;
      RestTemplate getNFTIDs = new RestTemplate();
      @SuppressWarnings("unchecked")
      ArrayList<String> responseIDs = getNFTIDs.getForObject(uriIDs, ArrayList.class);
      int numNFTs = responseIDs.size();
      model.addAttribute("numNFTs", numNFTs);

      return "account";
    }

}
