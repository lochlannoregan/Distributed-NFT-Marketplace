package service.account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;  

    @PostMapping("/signup")
    String createUser(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password) {
        try {
            userRepository.save(new NFTUser(username, new BCryptPasswordEncoder().encode(password), new BigDecimal("1000"), new ArrayList<String>()));
        } catch (IllegalArgumentException e) {
            return "exception";
        }
        return "OK";
    }

    @GetMapping("/get-nfts")
    List<String> getNFTs(@RequestParam(name = "username") String username) {
        NFTUser user = userRepository.findByUsername(username);
        return user.getNftsOwned();            
    }

    @GetMapping("/get-balance")
    BigDecimal getBalance(@RequestParam(name = "username") String username) {
        NFTUser user = userRepository.findByUsername(username);
        return user.getBalance();            
    }

    @PostMapping("/nft-sold")
    String nftSold(@RequestParam(name = "username") String username, @RequestParam(name = "imageID") String imageID, @RequestParam(name = "amount") BigDecimal amount) {
        try {
            NFTUser user = userRepository.findByUsername(username);
            user.setBalance(user.getBalance().add(amount));
            user.getNftsOwned().remove(imageID);
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            return "exception";
        }
        return "OK";
    }

    @PostMapping("/nft-added")
    String nftAdded(@RequestParam(name = "username") String username, @RequestParam(name = "imageID") String imageID) {
        try {
            NFTUser user = userRepository.findByUsername(username);
            user.getNftsOwned().add(imageID);
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            return "exception";
        }
        return "OK";
    }
    
}