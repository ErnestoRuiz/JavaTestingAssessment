package co.newlabs.unitTests;

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
import co.newlabs.service.PalletService;
import ma.glasnost.orika.MapperFacade;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PalletServiceTests {

    @Mock
    private ItemRepository itemRepo;

    @Mock
    private PalletRepository palletRepo;

    @Mock
    private MapperFacade mapper;

    @Mock
    private AccountClient accountClient;

    @InjectMocks
    private PalletService systemUnderTest;

    @Test
    public void getPalletById_ScenarioA() {
        //arrange
        ItemEntity itemEntity = ItemEntity.builder()
                .itemId(1)
                .palletId(1)
                .weight(1.11)
                .product("stuff")
                .build();

        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(itemEntity);

        ItemDTO itemDTO = ItemDTO.builder()
                .itemId(1)
                .weight(1.11)
                .product("stuff")
                .build();

        List<ItemDTO> itemDTOS = new ArrayList<>();
        itemDTOS.add(itemDTO);

        doReturn(itemDTOS).when(mapper).mapAsList(itemEntities, ItemDTO.class);

        PalletEntity palletEntity = PalletEntity.builder()
                .palletId(1)
                .accountId(1)
                .build();


        AccountDTO accountDTO = AccountDTO.builder()
                .accountId(1)
                .address("123 Fake st.")
                .city("Townsville")
                .state("Statesoda")
                .zip("12345")
                .build();

        PalletDTO palletDTO = PalletDTO.builder()
                .palletId(1)
                .accountId(1)
                .build();

        doReturn(palletDTO).when(mapper).map(palletEntity, PalletDTO.class);


        doReturn(palletEntity).when(palletRepo).getPalletById(palletEntity.getPalletId());
        doReturn(itemEntities).when(itemRepo).getItemsByPalletId(palletEntity.getPalletId());
        doReturn(accountDTO).when(accountClient).getAccountDetailsById(palletEntity.getPalletId());

        //act
        PalletDTO actualPallet = systemUnderTest.getPalletById(palletEntity.getPalletId());

        //assert
        Assert.assertThat(actualPallet.getPalletId(), is(palletDTO.getPalletId()));
        Assert.assertThat(actualPallet.getAccountId(), is(palletDTO.getAccountId()));
        Assert.assertThat(actualPallet.getCurrentWeight(), is(palletDTO.getCurrentWeight()));
        Assert.assertThat(actualPallet.getDestination(), is(palletDTO.getDestination()));
        Assert.assertThat(actualPallet.getItems(), is(palletDTO.getItems()));

        //verify
        verify(itemRepo, times(1)).getItemsByPalletId(1);
        verifyNoMoreInteractions(itemRepo);
        verify(palletRepo, times(1)).getPalletById(1);
        verifyNoMoreInteractions(palletRepo);
        verify(accountClient, times(1)).getAccountDetailsById(1);
        verifyNoMoreInteractions(accountClient);
        verify(mapper, times(1)).mapAsList(itemEntities, ItemDTO.class);
        verify(mapper, times(1)).map(palletEntity, PalletDTO.class);
        verifyNoMoreInteractions(mapper);
    }

    @Test
    public void getPalletById_ScenarioB() throws AccountAccessException {
        //arrange
        ItemEntity itemEntity = ItemEntity.builder()
                .itemId(1)
                .palletId(1)
                .weight(1.11)
                .product("stuff")
                .build();

        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(itemEntity);

        ItemDTO itemDTO = ItemDTO.builder()
                .itemId(1)
                .weight(1.11)
                .product("stuff")
                .build();

        List<ItemDTO> itemDTOS = new ArrayList<>();
        itemDTOS.add(itemDTO);

        doReturn(itemDTOS).when(mapper).mapAsList(itemEntities, ItemDTO.class);

        PalletEntity palletEntity = PalletEntity.builder()
                .palletId(1)
                .accountId(1)
                .build();

        PalletDTO palletDTO = PalletDTO.builder()
                .palletId(1)
                .accountId(1)
                .build();

        doReturn(palletDTO).when(mapper).map(palletEntity, PalletDTO.class);


        doReturn(palletEntity).when(palletRepo).getPalletById(palletEntity.getPalletId());
        doReturn(itemEntities).when(itemRepo).getItemsByPalletId(palletEntity.getPalletId());
        doThrow(AccountAccessException.class).when(accountClient).getAccountDetailsById(1);

        //act
        ItemDTO expectedItem = ItemDTO.builder()
                .itemId(1)
                .weight(1.11)
                .product("stuff")
                .build();

        List<ItemDTO> expectedList = new ArrayList<>();
        expectedList.add(expectedItem);

        PalletDTO expectedPallet = PalletDTO.builder()
                .palletId(1)
                .currentWeight(1.11)
                .items(expectedList)
                .destination("")
                .accountId(1)
                .build();

        PalletDTO actual = systemUnderTest.getPalletById(1);

        //assert
        Assert.assertThat(actual, is(equalTo(expectedPallet)));
        //verify
    }

    @Test
    public void addItemToTest_ScenarioA() {
        //arrange
        ItemDTO returnItem = ItemDTO.builder()
                .weight(1.11)
                .product("stuff: Boogaloo Unplugged")
                .itemId(1)
                .build();

        List<ItemDTO> returnItems = new ArrayList<>();
        returnItems.add(returnItem);

        PalletDTO returnPallet = PalletDTO.builder()
                .palletId(1)
                .accountId(1)
                .destination("123 fake st Townsville, Statesoda 12345")
                .currentWeight(1.11)
                .items(returnItems)
                .build();

        AccountDTO returnAccount = AccountDTO.builder()
                .accountName("someCorp")
                .state("Statesoda")
                .city("Townsville")
                .address("123 fake st")
                .accountId(1)
                .zip("12345")
                .build();

        doReturn(returnAccount).when(accountClient).getAccountDetailsById(returnPallet.getPalletId());

        PalletEntity palletEntity = PalletEntity.builder()
                .palletId(1)
                .accountId(1)
                .build();

        ItemEntity itemEntity = ItemEntity.builder()
                .itemId(1)
                .product("stuff: Boogaloo Unplugged")
                .weight(1.11)
                .palletId(1)
                .build();

        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(itemEntity);

        doReturn(palletEntity).when(palletRepo).getPalletById(returnPallet.getPalletId());
        doReturn(itemEntities).when(itemRepo).getItemsByPalletId(returnPallet.getPalletId());

        PalletDTO mappedPallet = PalletDTO.builder()
                .currentWeight(1.11)
                .accountId(1)
                .palletId(1)
                .build();

        doReturn(mappedPallet).when(mapper).map(palletEntity, PalletDTO.class);

        ItemDTO mappedItem = ItemDTO.builder()
                .itemId(1)
                .product("stuff: Boogaloo Unplugged")
                .weight(1.11)
                .build();


        List<ItemDTO> mappedItems = new ArrayList<>();
        mappedItems.add(mappedItem);

        doReturn(mappedItems).when(mapper).mapAsList(itemEntities, ItemDTO.class);

        ItemDTO addedItemDTO = ItemDTO.builder()
                .product("stuff 2: Electric Boogaloo")
                .weight(1.11)
                .build();

        ItemEntity addedItemEntity = ItemEntity.builder()
                .weight(2.22)
                .product("stuff 2: Electric Boogaloo")
                .build();

        doReturn(addedItemEntity).when(mapper).map(addedItemDTO, ItemEntity.class);

        doNothing().when(itemRepo).saveItem(addedItemEntity);

        //act
        PalletDTO expected = PalletDTO.builder()
                .palletId(1)
                .accountId(1)
                .currentWeight(1.11)
                .destination("123 fake st Townsville, Statesoda 12345")
                .items(mappedItems)
                .build();

        PalletDTO actual = systemUnderTest.addItemToPallet(addedItemDTO, 1);

        //assert
        Assert.assertThat(actual, is(equalTo(expected)));

        //verify
        verify(accountClient, times(1)).getAccountDetailsById(1);
        verifyNoMoreInteractions(accountClient);
        verify(palletRepo, times(1)).getPalletById(1);
        verifyNoMoreInteractions(palletRepo);
        verify(itemRepo, times(1)).getItemsByPalletId(1);
        verify(itemRepo, times(1)).saveItem(addedItemEntity);
        verifyNoMoreInteractions(itemRepo);
        verify(mapper, times(1)).map(palletEntity, PalletDTO.class);
        verify(mapper, times(1)).mapAsList(itemEntities, ItemDTO.class);
        verify(mapper, times(1)).map(addedItemDTO, ItemEntity.class);
        verifyNoMoreInteractions(mapper);
    }

    @Test(expected = PalletMaxWeightException.class)
    public void addItemToPallet_ScenarioB() throws PalletMaxWeightException {
        //arrange
        ItemDTO returnItem = ItemDTO.builder()
                .weight(1.11)
                .product("stuff: Boogaloo Unplugged")
                .itemId(1)
                .build();

        List<ItemDTO> returnItems = new ArrayList<>();
        returnItems.add(returnItem);

        PalletDTO returnPallet = PalletDTO.builder()
                .palletId(1)
                .accountId(1)
                .destination("123 fake st Townsville, Statesoda 12345")
                .currentWeight(1.11)
                .items(returnItems)
                .build();

        AccountDTO returnAccount = AccountDTO.builder()
                .accountName("someCorp")
                .state("Statesoda")
                .city("Townsville")
                .address("123 fake st")
                .accountId(1)
                .zip("12345")
                .build();

        doReturn(returnAccount).when(accountClient).getAccountDetailsById(returnPallet.getPalletId());

        PalletEntity palletEntity = PalletEntity.builder()
                .palletId(1)
                .accountId(1)
                .build();

        ItemEntity itemEntity = ItemEntity.builder()
                .itemId(1)
                .product("stuff: Boogaloo Unplugged")
                .weight(1.11)
                .palletId(1)
                .build();

        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(itemEntity);

        doReturn(palletEntity).when(palletRepo).getPalletById(returnPallet.getPalletId());
        doReturn(itemEntities).when(itemRepo).getItemsByPalletId(returnPallet.getPalletId());

        PalletDTO mappedPallet = PalletDTO.builder()
                .currentWeight(1.11)
                .accountId(1)
                .palletId(1)
                .build();

        doReturn(mappedPallet).when(mapper).map(palletEntity, PalletDTO.class);

        ItemDTO mappedItem = ItemDTO.builder()
                .itemId(1)
                .product("stuff: Boogaloo Unplugged")
                .weight(1.11)
                .build();


        List<ItemDTO> mappedItems = new ArrayList<>();
        mappedItems.add(mappedItem);

        doReturn(mappedItems).when(mapper).mapAsList(itemEntities, ItemDTO.class);

        ItemDTO addedItemDTO = ItemDTO.builder()
                .product("stuff 2: Electric Boogaloo")
                .weight(4600d)
                .build();

        //act
        PalletDTO actual = systemUnderTest.addItemToPallet(addedItemDTO, 1);

        //assert

        //verify
    }

    @Test
    public void removeItemFromPallet_ScenarioA() {
        //arrange
        ItemDTO returnItem = ItemDTO.builder()
                .weight(1.11)
                .product("stuff: Boogaloo Unplugged")
                .itemId(1)
                .build();

        List<ItemDTO> returnItems = new ArrayList<>();
        returnItems.add(returnItem);

        PalletDTO returnPallet = PalletDTO.builder()
                .palletId(1)
                .accountId(1)
                .destination("123 fake st Townsville, Statesoda 12345")
                .currentWeight(1.11)
                .items(returnItems)
                .build();

        AccountDTO returnAccount = AccountDTO.builder()
                .accountName("someCorp")
                .state("Statesoda")
                .city("Townsville")
                .address("123 fake st")
                .accountId(1)
                .zip("12345")
                .build();

        doReturn(returnAccount).when(accountClient).getAccountDetailsById(returnPallet.getPalletId());

        PalletEntity palletEntity = PalletEntity.builder()
                .palletId(1)
                .accountId(1)
                .build();

        ItemEntity itemEntity = ItemEntity.builder()
                .itemId(1)
                .product("stuff: Boogaloo Unplugged")
                .weight(1.11)
                .palletId(1)
                .build();

        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(itemEntity);

        doReturn(palletEntity).when(palletRepo).getPalletById(returnPallet.getPalletId());
        doReturn(itemEntities).when(itemRepo).getItemsByPalletId(returnPallet.getPalletId());

        PalletDTO mappedPallet = PalletDTO.builder()
                .currentWeight(1.11)
                .accountId(1)
                .palletId(1)
                .build();

        doReturn(mappedPallet).when(mapper).map(palletEntity, PalletDTO.class);

        ItemDTO mappedItem = ItemDTO.builder()
                .itemId(1)
                .product("stuff: Boogaloo Unplugged")
                .weight(1.11)
                .build();


        List<ItemDTO> mappedItems = new ArrayList<>();
        mappedItems.add(mappedItem);

        doReturn(mappedItems).when(mapper).mapAsList(itemEntities, ItemDTO.class);

        doNothing().when(itemRepo).removeItem(1);

        //act

        List<ItemDTO> expectedList = new ArrayList<>();

        PalletDTO expectedPallet = PalletDTO.builder()
                .palletId(1)
                .accountId(1)
                .items(expectedList)
                .destination("123 fake st Townsville, Statesoda 12345")
                .currentWeight(1.11)
                .build();

        PalletDTO actualPallet = systemUnderTest.removeItemFromPallet(1,1);

        //assert
        Assert.assertThat(actualPallet, is(equalTo(expectedPallet)));

        //verify
        verify(accountClient, times(1)).getAccountDetailsById(1);
        verifyNoMoreInteractions(accountClient);
        verify(palletRepo, times(1)).getPalletById(1);
        verifyNoMoreInteractions(palletRepo);
        verify(itemRepo, times(1)).getItemsByPalletId(1);
        verify(itemRepo, times(1)).removeItem(1);
        verifyNoMoreInteractions(itemRepo);
        verify(mapper, times(1)).map(palletEntity, PalletDTO.class);
        verify(mapper, times(1)).mapAsList(itemEntities, ItemDTO.class);
        verifyNoMoreInteractions(mapper);
    }

    @Test(expected = ItemNotOnPalletException.class)
    public void removeItemFromPallet_ScenarioB() throws ItemNotOnPalletException{
        //arrange
        ItemDTO returnItem = ItemDTO.builder()
                .weight(1.11)
                .product("stuff: Boogaloo Unplugged")
                .itemId(1)
                .build();

        List<ItemDTO> returnItems = new ArrayList<>();
        returnItems.add(returnItem);

        PalletDTO returnPallet = PalletDTO.builder()
                .palletId(1)
                .accountId(1)
                .destination("123 fake st Townsville, Statesoda 12345")
                .currentWeight(1.11)
                .items(returnItems)
                .build();

        AccountDTO returnAccount = AccountDTO.builder()
                .accountName("someCorp")
                .state("Statesoda")
                .city("Townsville")
                .address("123 fake st")
                .accountId(1)
                .zip("12345")
                .build();

        doReturn(returnAccount).when(accountClient).getAccountDetailsById(returnPallet.getPalletId());

        PalletEntity palletEntity = PalletEntity.builder()
                .palletId(1)
                .accountId(1)
                .build();

        ItemEntity itemEntity = ItemEntity.builder()
                .itemId(1)
                .product("stuff: Boogaloo Unplugged")
                .weight(1.11)
                .palletId(1)
                .build();

        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(itemEntity);

        doReturn(palletEntity).when(palletRepo).getPalletById(returnPallet.getPalletId());
        doReturn(itemEntities).when(itemRepo).getItemsByPalletId(returnPallet.getPalletId());

        PalletDTO mappedPallet = PalletDTO.builder()
                .currentWeight(1.11)
                .accountId(1)
                .palletId(1)
                .build();

        doReturn(mappedPallet).when(mapper).map(palletEntity, PalletDTO.class);

        ItemDTO mappedItem = ItemDTO.builder()
                .itemId(1)
                .product("stuff: Boogaloo Unplugged")
                .weight(1.11)
                .build();


        List<ItemDTO> mappedItems = new ArrayList<>();
        mappedItems.add(mappedItem);

        doReturn(mappedItems).when(mapper).mapAsList(itemEntities, ItemDTO.class);

        //act
        PalletDTO actual = systemUnderTest.removeItemFromPallet(2, 1);
        //assert

        //verify

    }
}
