package tn.itss.iris.model;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseModel {
	
	private HttpStatus httpStatus;
	private LocalDateTime timestamp;
	private String message;
	private Object details;

	public ResponseModel() {
		super();
		this.httpStatus = HttpStatus.OK;
		this.timestamp = LocalDateTime.now();
		this.message = "";
		this.details = null;
	}
	
	
}
