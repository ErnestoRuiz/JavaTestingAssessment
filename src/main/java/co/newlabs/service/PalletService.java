package co.newlabs.service;

import co.newlabs.client.account.AccountClient;
import co.newlabs.client.account.AccountDTO;
import co.newlabs.dto.ItemDTO;
import co.newlabs.dto.PalletDTO;
import co.newlabs.exception.AccountAccessException;
import co.newlabs.exception.ItemNotOnPalletException;
import co.newlabs.exception.PalletMaxWeightException;
import co.newlabs.repository.item.ItemEntity;
import co.newlabs.repository.item.ItemRepository;
import co.newlabs.repository.pallet.PalletEntity;
import co.newlabs.repository.pallet.PalletRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class PalletService {
    private PalletRepository palletRepository;
    private ItemRepository itemRepository;
    private AccountClient accountClient;
    private MapperFacade mapper;

    public PalletDTO getPalletById(final int id) {
        PalletEntity pEntity = palletRepository.getPalletById(id);
        List<ItemEntity> itemList = itemRepository.getItemsByPalletId(id);

        PalletDTO pallet = mapper.map(pEntity, PalletDTO.class);
        pallet.setItems(mapper.mapAsList(itemList, ItemDTO.class));
        double accumulator = 0d;
        for (ItemDTO item: pallet.getItems()) {
            accumulator += item.getWeight();
        }
        pallet.setCurrentWeight(accumulator);
        try {
            AccountDTO account = accountClient.getAccountDetailsById(pallet.getAccountId());
            pallet.setDestination(account.getMailingAddress());
        } catch (AccountAccessException ex) {
            log.warn("Unable to access account information.");
            pallet.setDestination("");
        }
        return pallet;
    }


    public PalletDTO addItemToPallet(final ItemDTO item, final int palletId) {
        PalletDTO pallet = getPalletById(palletId);
        if (pallet.getCurrentWeight() + item.getWeight() > PalletDTO.MAX_WEIGHT) {
            throw new PalletMaxWeightException(pallet.getCurrentWeight(), item.getWeight());
        }
        ItemEntity entity = mapper.map(item, ItemEntity.class);
        entity.setPalletId(palletId);
        itemRepository.saveItem(entity);
        pallet.getItems().add(item);
        return pallet;
    }

    public PalletDTO removeItemFromPallet(final int itemId, final int palletId) {
        PalletDTO pallet = getPalletById(palletId);
        ItemDTO item = pallet.getItems().stream()
                .filter(i -> i.getItemId() == itemId)
                .findFirst()
                .orElseThrow(() -> new ItemNotOnPalletException(itemId));
        itemRepository.removeItem(itemId);
        pallet.getItems().removeIf(i -> i.getItemId() == itemId);
        return pallet;
    }
}
