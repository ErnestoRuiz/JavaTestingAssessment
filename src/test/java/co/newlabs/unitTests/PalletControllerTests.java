package co.newlabs.unitTests;

import co.newlabs.controller.PalletController;
import co.newlabs.dto.ItemDTO;
import co.newlabs.dto.PalletDTO;
import co.newlabs.service.PalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PalletController.class)
public class PalletControllerTests {

    @MockBean
    private PalletService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void getPalletById_ScenarioA() throws Exception {
        //arrange
        PalletDTO expectedPallet = PalletDTO.builder()
                .palletId(1)
                .destination("home")
                .build();

        doReturn(expectedPallet).when(service).getPalletById(expectedPallet.getPalletId());

        String expectedResult = objectMapper.writeValueAsString(expectedPallet);

        //act
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/pallet/1"))
                .andExpect(status().is(200))
                .andReturn();

        String actualResult = result.getResponse().getContentAsString();

        //assert

        Assert.assertThat(actualResult, is(equalTo(expectedResult)));

        //verify
        verify(service, times(1)).getPalletById(1);
        verifyNoMoreInteractions(service);
    }

    @Test
    public void addItemToPallet_ScenarioA() throws Exception {
        //arrange
        ItemDTO mockItem = ItemDTO.builder()
                .itemId(1)
                .product("stuff")
                .weight(1.11)
                .build();

        List<ItemDTO> mockList = new ArrayList<>();
        mockList.add(mockItem);

        PalletDTO mockPallet = PalletDTO.builder()
                .palletId(1)
                .items(mockList)
                .destination("home")
                .currentWeight(1.11)
                .accountId(1)
                .build();

        doReturn(mockPallet).when(service).addItemToPallet(mockItem, mockPallet.getPalletId());

        String requestBody = objectMapper.writeValueAsString(mockItem);

        //act
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/pallet/1/add").content(requestBody).contentType("application/json"))
                .andExpect(status().is(200))
                .andReturn();

        String actualResult = result.getResponse().getContentAsString();

        String expectedResult = objectMapper.writeValueAsString(mockPallet);

        //assert
        Assert.assertThat(actualResult, is(equalTo(expectedResult)));

        //verify

        verify(service, times(1)).addItemToPallet(mockItem, mockPallet.getPalletId());
        verifyNoMoreInteractions(service);
    }

    @Test
    public void removeItemFromPallet() throws Exception{

        //arrange
        PalletDTO expectedPallet = PalletDTO.builder()
                .palletId(1)
                .build();

        ItemDTO mockItem = ItemDTO.builder()
                .itemId(1)
                .build();

        doReturn(expectedPallet).when(service).removeItemFromPallet(mockItem.getItemId(), expectedPallet.getPalletId());

        //act
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/pallet/1/remove/1"))
                .andExpect(status().is(200))
                .andReturn();

        String actualResult = result.getResponse().getContentAsString();
        String expectedResult = objectMapper.writeValueAsString(expectedPallet);

        //assert

        Assert.assertThat(actualResult, is(equalTo(expectedResult)));

        //verify

        verify(service, times(1)).removeItemFromPallet(mockItem.getItemId(), expectedPallet.getPalletId());
    }
}
