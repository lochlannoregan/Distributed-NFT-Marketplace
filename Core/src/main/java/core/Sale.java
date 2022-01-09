package core;

import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Sale {
  private BigDecimal value;
  private String message;
}
