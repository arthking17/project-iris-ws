package tn.itss.iris.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document(collection = "transaction")
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
	
	@Transient
	public static final String SEQUENCE_NAME = "transactions_sequence";
	
	@Id
	private Long transactionId;
	
	private Long accountId;
	
	private Long customerId;
	
	private float amount;
	
	private int transactionStatus;
	
	private Date transactionDate;
	
	private Date createdDate;
	
	private Date updatedDate;
	
	private String currency;
	
	private float localCurrencyAmount;
	
	private String foreignCurrencyCountry;
	
	private String transactionDescription;
}
