package tn.itss.iris.resource;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import tn.itss.iris.model.ResponseModel;

@RestControllerAdvice
public class ValidationHandler extends ResponseEntityExceptionHandler {

	private static final Logger l = LogManager.getLogger(ValidationHandler.class);

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus httpStatus, WebRequest request) {

		Map<String, Object> errors = new HashMap<>();
		l.info("\n ValidationHandler : All exceptions handle on request - " + request + " : " + ex);
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String message = error.getDefaultMessage();
			errors.put(fieldName, message);
		});

		String message = ex.getClass() + "; Number of Errors : " + ex.getErrorCount();

		ResponseModel responseError = new ResponseModel(httpStatus, LocalDateTime.now(), message, errors);
		return new ResponseEntity<Object>(responseError, HttpStatus.BAD_REQUEST);

	}

	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex,
			HttpHeaders headers, HttpStatus httpStatus, WebRequest request){
		
		l.info("\n ValidationHandler : All exceptions handle on request - " + request + " : " + ex);

		String message = ex.getClass().toString();

		ResponseModel responseError = new ResponseModel(httpStatus, LocalDateTime.now(), message, ex.getLocalizedMessage());
		return new ResponseEntity<Object>(responseError, HttpStatus.BAD_REQUEST);
	}
}
