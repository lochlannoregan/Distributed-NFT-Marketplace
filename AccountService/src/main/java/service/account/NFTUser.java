package service.account;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
public class NFTUser {

    @Id
    private String id;
    private String username;
    private String password;
    private BigDecimal balance;
    private List<String> nftsOwned;

    public NFTUser(String username, String password, BigDecimal balance, List<String> nftsOwned) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.nftsOwned = nftsOwned;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<String> getNftsOwned() {
        return nftsOwned;
    }

    public void setNftsOwned(List<String> nftsOwned) {
        this.nftsOwned = nftsOwned;
    }

}
