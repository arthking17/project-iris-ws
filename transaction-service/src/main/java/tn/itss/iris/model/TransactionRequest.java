package tn.itss.iris.model;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

	private String transactionId;

	private String accountId;

	private String customerId;

	private String amount;

	private String transactionStatus;

	private String transactionDate;

	private String createdDate;

	private String updatedDate;

	private String currency;

	private String localCurrencyAmount;

	private String foreignCurrencyCountry;

	private String transactionDescription;

	public void TransactionToTransactionRequest(Transaction transaction) {
		this.setTransactionId(String.valueOf(transaction.getTransactionId()));
		this.setAccountId(String.valueOf(transaction.getAccountId()));
		this.setCustomerId(String.valueOf(transaction.getCustomerId()));

		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "CM"));
		this.setAmount(currencyFormat.format(transaction.getAmount()));
		
		this.setTransactionStatus(String.valueOf(transaction.getTransactionStatus()));

		DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
		if (transaction.getCreatedDate() != null)
			this.setCreatedDate(dateFormat.format(transaction.getCreatedDate()));
		if (transaction.getUpdatedDate() != null)
			this.setUpdatedDate(dateFormat.format(transaction.getUpdatedDate()));
		this.setTransactionDate(dateFormat.format(transaction.getTransactionDate()));

		this.setCurrency(transaction.getCurrency());
		
		this.setLocalCurrencyAmount(currencyFormat.format(transaction.getLocalCurrencyAmount()));
		currencyFormat.format(transaction.getLocalCurrencyAmount());
		
		this.setForeignCurrencyCountry(transaction.getForeignCurrencyCountry());
		this.setTransactionDescription(transaction.getTransactionDescription());

	}

	public Transaction TransactionRequestToTransaction() throws ParseException {
		Transaction transaction = new Transaction();

		transaction.setTransactionId(Long.valueOf(this.getTransactionId()));
		transaction.setAccountId(Long.valueOf(this.getAccountId()));
		transaction.setCustomerId(Long.valueOf(this.getCustomerId()));
		transaction.setAmount(Float.valueOf(this.getAmount()));
		transaction.setTransactionStatus(Integer.valueOf(this.getTransactionStatus()));

		DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
		if (this.getCreatedDate() != null && !this.getCreatedDate().equals(""))
			transaction.setCreatedDate(dateFormat.parse(this.getCreatedDate()));
		if (this.getUpdatedDate() != null && !this.getUpdatedDate().equals(""))
			transaction.setUpdatedDate(dateFormat.parse(this.getUpdatedDate()));
		transaction.setTransactionDate(dateFormat.parse(this.getTransactionDate()));

		transaction.setCurrency(this.getCurrency());
		
		transaction.setLocalCurrencyAmount(Float.valueOf(this.getLocalCurrencyAmount()));
		transaction.setForeignCurrencyCountry(this.getForeignCurrencyCountry());
		transaction.setTransactionDescription(this.getTransactionDescription());

		return transaction;
	}

}
