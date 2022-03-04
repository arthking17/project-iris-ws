package tn.itss.iris.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document(collection = "accounts")
@NoArgsConstructor
@AllArgsConstructor
public class Account {

	@Transient
	public static final String SEQUENCE_NAME = "accounts_sequence";
	
	@Id
	private Long accountId;

	private Long customerId;

	private String currency;

	private String accountName;
	
	
}
