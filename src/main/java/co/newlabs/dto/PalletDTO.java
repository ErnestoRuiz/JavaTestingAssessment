package co.newlabs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PalletDTO {
    private int palletId;
    private int accountId;
    private String destination;
    private List<ItemDTO> items;
    private double currentWeight;

    public static final double MAX_WEIGHT = 4600d;
}
