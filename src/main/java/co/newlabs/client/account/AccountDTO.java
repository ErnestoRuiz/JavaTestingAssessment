package co.newlabs.client.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private int accountId;
    private String accountName;
    private String address;
    private String city;
    private String state;
    private String zip;

    public String getMailingAddress() {
        return this.address + " " + this.city + ", " + this.state + " " + this.zip;
    }
}
