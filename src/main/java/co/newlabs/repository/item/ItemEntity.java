package co.newlabs.repository.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemEntity {
    private int itemId;
    private int palletId;
    private double weight;
    private String product;
}
