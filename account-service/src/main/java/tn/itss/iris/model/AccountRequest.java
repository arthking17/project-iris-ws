package tn.itss.iris.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {

	@Nullable
	@Pattern(regexp = "^[\\d]*$", message = "accountId must be an integer")
	private String accountId;

	@NotBlank
	@Pattern(regexp = "^[\\d]*$", message = "accountId must be an integer")
	private String customerId;

	@NotBlank
	@Pattern(regexp = "^[A-Z]{2,4}$|^[A-Z]{4,5}[ ][A-Z]{3}$", message = "invalid pattern for currency")
	private String currency;

	@Pattern(regexp = "^[A-Z][\\d,a-z,A-Z]+$", message = "invalid pattern for currency")
	private String accountName;

	public void AccountToAccountRequest(Account account) {
		this.setAccountId(account.getAccountId().toString());
		this.setCustomerId(account.getCustomerId().toString());
		this.setCurrency(account.getCurrency());
		this.setAccountName(account.getAccountName());
	}

	public Account AccountRequestToAccount() {
		Account account = new Account ();
		
		account.setAccountId(Long.parseLong(this.getAccountId()));
		account.setCustomerId(Long.parseLong(this.getCustomerId()));
		account.setCurrency(this.getCurrency());
		account.setAccountName(this.getAccountName());
		
		return account;
	}
}
