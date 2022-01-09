package service.account;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<NFTUser, String>{

    NFTUser findByUsername(String username);
    
}
