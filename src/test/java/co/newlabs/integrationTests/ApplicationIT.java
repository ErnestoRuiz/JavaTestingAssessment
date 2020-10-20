package co.newlabs.integrationTests;

import co.newlabs.client.account.AccountClient;
import co.newlabs.client.account.AccountDTO;
import co.newlabs.dto.ItemDTO;
import co.newlabs.dto.PalletDTO;
import co.newlabs.exception.AccountAccessException;
import co.newlabs.repository.pallet.PalletEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
public class ApplicationIT {
    @LocalServerPort
    private int REST_ASSURED_PORT_NUMBER;

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp(){
        wireMockServer.resetAll();
        RestAssured.port = REST_ASSURED_PORT_NUMBER;
        RestAssured.baseURI = "http://localhost:" + REST_ASSURED_PORT_NUMBER;
    }

    @Test
    public void getPalletById_ScenarioA() throws Exception {
        //arrange
        AccountDTO mockAccount = AccountDTO.builder()
                .accountId(1)
                .accountName("someCorp")
                .address("123 Fake St")
                .city("Townsville")
                .state("Statesoda")
                .zip("12345")
                .build();

        wireMockServer.stubFor(get(urlMatching("/account/api/active/1"))
            .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-type", "application/json")
            .withBody(objectMapper.writeValueAsString(mockAccount))
            )
        );

        //act
        RequestSpecification request = given();
        Response response = request.get("/pallet/1");

        //assert
        List<ItemDTO> expectedList = new ArrayList<>();
        expectedList.add(ItemDTO.builder().itemId(1).product("stuff").weight(1.11).build());

        PalletDTO expectedPallet = PalletDTO.builder()
                .palletId(1)
                .accountId(1)
                .currentWeight(1.11)
                .destination("123 Fake St Townsville, Statesoda 12345")
                .items(expectedList)
                .build();

        PalletDTO actualPallet = objectMapper.readValue(response.getBody().print(), PalletDTO.class);

        Assert.assertThat(actualPallet, is(equalTo(expectedPallet)));
        Assert.assertThat(response.getStatusCode(), is(200));
        Assert.assertThat(wireMockServer.findAllUnmatchedRequests().size(), is(0));

        //verify

        wireMockServer.verify(1, getRequestedFor(urlMatching("/account/api/active/1")));
    }

    @Test
    public void addItemToPallet_ScenarioA() throws Exception {
        //arrange

        AccountDTO mockAccount = AccountDTO.builder()
                .accountId(2)
                .accountName("someCorp")
                .address("123 Fake St")
                .city("Townsville")
                .state("Statesoda")
                .zip("12345")
                .build();

        wireMockServer.stubFor(get(urlMatching("/account/api/active/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-type", "application/json")
                        .withBody(objectMapper.writeValueAsString(mockAccount))
                )
        );



        ItemDTO mockItem = ItemDTO.builder()
                .itemId(2)
                .weight(25.34)
                .product("Bubbly")
                .build();

        //act

        RequestSpecification request = given();
        request.contentType("application/json").body(objectMapper.writeValueAsString(mockItem));
        Response response = request.post("/pallet/1/add");


        //assert
        ItemDTO repoItem = ItemDTO.builder()
                .itemId(1)
                .weight(1.11)
                .product("stuff")
                .build();

        ItemDTO expectedItem = ItemDTO.builder()
                .itemId(2)
                .weight(25.34)
                .product("Bubbly")
                .build();

        List<ItemDTO> expectedItems = new ArrayList<>();
        expectedItems.add(repoItem);
        expectedItems.add(expectedItem);

        PalletDTO expectedPallet = PalletDTO.builder()
                .accountId(1)
                .palletId(1)
                .items(expectedItems)
                .destination("123 Fake St Townsville, Statesoda 12345")
                .currentWeight(1.11)
                .build();

        PalletDTO actualPallet = objectMapper.readValue(response.getBody().print(), PalletDTO.class);

        Assert.assertThat(actualPallet, is(equalTo(expectedPallet)));
        Assert.assertThat(response.getStatusCode(), is(200));
        Assert.assertThat(wireMockServer.findAllUnmatchedRequests().size(), is(0));

        //verify
        wireMockServer.verify(1, getRequestedFor(urlMatching("/account/api/active/1")));
    }

    @Test
    public void removeItemFromPallet_ScenarioA() throws Exception {
        //arrange

        AccountDTO mockAccount = AccountDTO.builder()
                .accountId(1)
                .accountName("fakeCorp")
                .address("123 Fake St")
                .city("Townsville")
                .state("Statesoda")
                .zip("12345")
                .build();

        wireMockServer.stubFor(get(urlMatching("/account/api/active/2"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-type", "application/json")
                        .withBody(objectMapper.writeValueAsString(mockAccount))
                )
        );

        //act
        RequestSpecification request = given();
        Response response = request.get("/pallet/2/remove/2");

        //assert
        List<ItemDTO> expectedItems = new ArrayList<>();

        PalletDTO expectedPallet = PalletDTO.builder()
                .palletId(2)
                .accountId(2)
                .currentWeight(2.22)
                .items(expectedItems)
                .destination("123 Fake St Townsville, Statesoda 12345")
                .build();

        PalletDTO actualPallet = objectMapper.readValue(response.getBody().print(), PalletDTO.class);

        Assert.assertThat(actualPallet, is(equalTo(expectedPallet)));
        Assert.assertThat(response.getStatusCode(), is(200));
        Assert.assertThat(wireMockServer.findAllUnmatchedRequests().size(), is(0));

        //verify
        wireMockServer.verify(1, getRequestedFor(urlMatching("/account/api/active/2")));
    }

    @Test
    public void AccountClientException_ScenarioA() throws Exception {
        //arrange
        wireMockServer.stubFor(get(urlMatching("/account/api/active/1"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("activeAccount Error")
                )
        );

        AccountDTO mockAccount = AccountDTO.builder()
                .accountId(1)
                .accountName("fakeCorp")
                .address("123 Fake St")
                .city("Townsville")
                .state("Statesoda")
                .zip("12345")
                .build();

        wireMockServer.stubFor(get(urlMatching("/account/api/warehouse/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-type", "application/json")
                        .withBody(objectMapper.writeValueAsString(mockAccount))
                )
        );

        //act
        RequestSpecification request = given();
        Response response = request.get("/pallet/1");

        //assert

        ItemDTO expectedItem = ItemDTO.builder()
                .itemId(1)
                .weight(1.11)
                .product("stuff")
                .build();

        List<ItemDTO> expectedItems = Collections.singletonList(expectedItem);

        PalletDTO expectedPallet = PalletDTO.builder()
                .accountId(1)
                .destination("123 Fake St Townsville, Statesoda 12345")
                .items(expectedItems)
                .currentWeight(1.11)
                .palletId(1)
                .build();

        PalletDTO actualPallet = objectMapper.readValue(response.getBody().print(), PalletDTO.class);

        Assert.assertThat(actualPallet, is(equalTo(expectedPallet)));

        //verify

        wireMockServer.verify(1, getRequestedFor(urlMatching("/account/api/active/1")));
        wireMockServer.verify(1, getRequestedFor(urlMatching("/account/api/warehouse/1")));
    }

    @Test
    public void AccountClientException_ScenarioB() throws Exception {
        //arrange
        wireMockServer.stubFor(get(urlMatching("/account/api/active/1"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("activeAccount Error")
                )
        );

        AccountDTO mockAccount = AccountDTO.builder()
                .accountId(1)
                .accountName("fakeCorp")
                .address("123 Fake St")
                .city("Townsville")
                .state("Statesoda")
                .zip("12345")
                .build();

        wireMockServer.stubFor(get(urlMatching("/account/api/warehouse/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-type", "application/json")
                        .withBody(objectMapper.writeValueAsString(mockAccount))
                )
        );

        //act
        RequestSpecification request = given();
        Response response = request.get("/pallet/1");

        //assert

        ItemDTO expectedItem = ItemDTO.builder()
                .itemId(1)
                .weight(1.11)
                .product("stuff")
                .build();

        List<ItemDTO> expectedItems = Collections.singletonList(expectedItem);

        PalletDTO expectedPallet = PalletDTO.builder()
                .accountId(1)
                .destination("")
                .items(expectedItems)
                .currentWeight(1.11)
                .palletId(1)
                .build();

        PalletDTO actualPallet = objectMapper.readValue(response.getBody().print(), PalletDTO.class);

        Assert.assertThat(actualPallet, is(equalTo(expectedPallet)));

        //verify

        wireMockServer.verify(1, getRequestedFor(urlMatching("/account/api/active/1")));
    }

    @Test
    public void AccountClientException_ScenarioC() throws Exception {
        //arrange
        wireMockServer.stubFor(get(urlMatching("/account/api/active/1"))
                .willReturn(aResponse()
                        .withStatus(409)
                        .withBody("activeAccount Error")
                )
        );


        //act
        RequestSpecification request = given();
        Response response = request.get("/pallet/1");

        //assert

        ItemDTO expectedItem = ItemDTO.builder()
                .itemId(1)
                .weight(1.11)
                .product("stuff")
                .build();

        List<ItemDTO> expectedItems = Collections.singletonList(expectedItem);

        PalletDTO expectedPallet = PalletDTO.builder()
                .accountId(1)
                .destination("")
                .items(expectedItems)
                .currentWeight(1.11)
                .palletId(1)
                .build();

        PalletDTO actualPallet = objectMapper.readValue(response.getBody().print(), PalletDTO.class);

        Assert.assertThat(actualPallet, is(equalTo(expectedPallet)));

        //verify

        wireMockServer.verify(1, getRequestedFor(urlMatching("/account/api/active/1")));
    }
}
