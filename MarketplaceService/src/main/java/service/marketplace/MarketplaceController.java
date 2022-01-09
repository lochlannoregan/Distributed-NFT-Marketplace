package service.marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import core.Sale;

@RestController
public class MarketplaceController {

  @Autowired
  private MarketplaceService marketplaceService;

  @GetMapping("/sell")
  public ResponseEntity<Sale> sell() {
    return marketplaceService.sell();
  }

}

