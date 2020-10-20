package co.newlabs.client.account;

import co.newlabs.exception.AccountAccessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class AccountClient {
    private final String url;
    private final String warehouseurl;
    private final RestTemplate restTemplate;

    public AccountClient(@Value("${urls.activeaccounts}") String url, @Value("${urls.warehouseaccounts}") String warehouseurl, RestTemplate restTemplate) {
        this.url = url;
        this.warehouseurl = warehouseurl;
        this.restTemplate = restTemplate;
    }

    public AccountDTO getAccountDetailsById(final long id) {
        try {
            ResponseEntity<AccountDTO> response = restTemplate.getForEntity(url + id, AccountDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException clientEx) {
            if (HttpStatus.NOT_FOUND.equals(clientEx.getStatusCode())) {
                ResponseEntity<AccountDTO> response = restTemplate.getForEntity(warehouseurl + id, AccountDTO.class);
                return response.getBody();
            } else {
                throw new AccountAccessException();
            }
        } catch (HttpServerErrorException serverEx) {
            throw new AccountAccessException();
        }
    }
}
