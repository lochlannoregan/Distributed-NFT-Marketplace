package service.marketplace;

import java.math.BigDecimal;
import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import core.Sale;

@Service
public class MarketplaceService {

  public static final Sale[] sales_responses = {
		new Sale(new BigDecimal(100), "Wow! Great Sucess!"),
    new Sale(new BigDecimal(0), "This NFT is worthless, don't quit your day job!"),
    new Sale(new BigDecimal(0), "My dissapointment is immeasurable, and my day is ruined."),
    new Sale(new BigDecimal(-1000), "You can lose money doing this? Who knew."),
	};

  public ResponseEntity<Sale> sell() {
    // pick random response from array
    Random rand = new Random();
    int index = rand.nextInt(sales_responses.length);
    // return response
    return ResponseEntity.ok(sales_responses[index]);
  }

}