package tn.itss.iris.resource;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import tn.itss.iris.model.ResponseModel;
import tn.itss.iris.model.Transaction;
import tn.itss.iris.model.TransactionRequest;
import tn.itss.iris.service.ITransactionService;

@RestController
@RequestMapping("transactions")
public class TransactionResource {

	@Autowired
	ITransactionService transactionService;

	private static final Logger l = LogManager.getLogger(ITransactionService.class);

	/**
	 * @description retrieve all transactions from DB
	 * @author wi11i4m
	 * @return ResponseEntity
	 * @throws ParseException 
	 */
	@GetMapping
	public ResponseEntity<ResponseModel> getAllTransactions(
			@RequestParam(required = false, name = "pagination") Boolean pagination,
			@RequestParam(required = false, name = "size") Integer size,
			@RequestParam(required = false, name = "page") Integer page,
			@RequestParam(required = false, name = "orderBy") String orderBy,
			@RequestParam(required = false, name = "orderType") String orderType,
			@RequestParam(required = false, name = "currency") String currency,
			@RequestBody(required = false) TransactionRequest transactionRequest) throws ParseException {

		try {
			Object transactions = null;
			l.info(currency);
			if (transactionRequest == null)
				transactions = transactionService.getAllTransactions(pagination, size, page, orderBy, orderType, currency);
//			List<TransactionRequest> transactionResponses = transactionService.listTransactionsToListTransactionRequests(streams.stream());
			else {
				transactions = transactionService.getAllTransactions(pagination, size, page, orderBy, orderType,
						transactionRequest.TransactionRequestToTransaction());
				l.info("get All Transactions By transactionRequest - " + transactionRequest);
			}
			if (transactions != null)
				return ResponseEntity
						.ok(new ResponseModel(HttpStatus.OK, LocalDateTime.now(), "Transaction Found", transactions));
			else
				return ResponseEntity
						.ok(new ResponseModel(HttpStatus.OK, LocalDateTime.now(), "Transaction Not Found", "empty"));
		} catch (Exception e) {
			l.error(e);
			return ResponseEntity.ok(new ResponseModel(HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(),
					e.getClass().toString(), e.getMessage()));
		}
	}

	/**
	 * @description retrieve transaction by id
	 * @author wi11i4m
	 * @param id
	 * @return ResponseEntity
	 */
	@GetMapping("/{transaction_id}")
	public ResponseEntity<ResponseModel> getTransaction(@PathVariable("transaction_id") Long id) {

		try {
			Transaction transactionFound = transactionService.getTransaction(id);
			if (transactionFound != null) {

				// formatting Object Transaction to TransactionRequest
				TransactionRequest transactionResponse = new TransactionRequest();
				transactionResponse.TransactionToTransactionRequest(transactionFound);

				return ResponseEntity
						.ok(new ResponseModel(HttpStatus.OK, LocalDateTime.now(), "Transaction Found", transactionResponse));
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel(HttpStatus.BAD_REQUEST,
						LocalDateTime.now(), "Not Found!!!", "No Transaction with id " + id + " Found!"));
			}

		} catch (Exception e) {
			l.error(e);
			return ResponseEntity.ok(new ResponseModel(HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(),
					e.getClass().toString(), e.getMessage()));
		}
	}

	/**
	 * @description add new transaction to the DB
	 * @author wi11i4m
	 * @param TransactionRequest
	 * @return ResponseEntity
	 * @throws ParseException
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<ResponseModel> addTransaction(@Valid @RequestBody TransactionRequest transactionRequest)
			throws ParseException {

//		try {
			Transaction transaction = transactionRequest.TransactionRequestToTransaction();

			// Adding transaction to DB
			l.info(transaction);
			transaction.setCreatedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
			Transaction transactionAdded = transactionService.addTransaction(transaction);
			l.debug("Transaction added : " + transactionAdded);

			// formatting Object Transaction to TransactionRequest
			TransactionRequest transactionResponse = new TransactionRequest();
			transactionResponse.TransactionToTransactionRequest(transactionAdded);

			return ResponseEntity
					.ok(new ResponseModel(HttpStatus.CREATED, LocalDateTime.now(), "Transaction Added", transactionResponse));
//		} catch (Exception e) {
//			l.error(e);
//			return ResponseEntity.ok(new ResponseModel(HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(),
//					e.getClass().toString(), e.getMessage()));
//		}

	}

	/**
	 * @description update transaction
	 * @author wi11i4m
	 * @param TransactionRequest
	 * @return ResponseEntity
	 * @throws ParseException
	 */
	@PutMapping
	public ResponseEntity<ResponseModel> updateTransaction(@Valid @RequestBody TransactionRequest transactionRequest)
			throws ParseException {

		try {
			Transaction transaction = transactionRequest.TransactionRequestToTransaction();
			l.debug("Updating transaction : " + transaction);

			//update transaction in DB
			transaction.setUpdatedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
			Transaction transactionUpdated = transactionService.updateTransaction(transaction);
			l.debug("Transaction updated : " + transactionUpdated);

			// formatting Object Transaction to TransactionRequest
			TransactionRequest transactionResponse = new TransactionRequest();
			transactionResponse.TransactionToTransactionRequest(transactionUpdated);

			return ResponseEntity
					.ok(new ResponseModel(HttpStatus.OK, LocalDateTime.now(), "Transaction Updated", transactionResponse));

		} catch (Exception e) {
			l.error(e);
			return ResponseEntity.ok(new ResponseModel(HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(),
					e.getClass().toString(), e.getMessage()));
		}
	}

	/**
	 * @description delete single transaction by id
	 * @author wi11i4m
	 * @param id
	 * @return ResponseEntity
	 */
	@DeleteMapping("/{transaction_id}")
	public ResponseEntity<ResponseModel> deleteTransaction(@Valid @PathVariable("transaction_id") Long id) {

		try {
			Boolean deleteState = transactionService.deleteTransaction(id);
			if (deleteState)
				return ResponseEntity.ok(new ResponseModel(HttpStatus.OK, LocalDateTime.now(), "Transaction Deleted",
						"Transaction with id '" + id + "' deleted!"));
			else
				return ResponseEntity.ok(new ResponseModel(HttpStatus.BAD_REQUEST, LocalDateTime.now(),
						"Transaction Not Found", "Transaction with id '" + id + "' not found or already deleted!"));

		} catch (Exception e) {
			l.error(e);
			return ResponseEntity.ok(new ResponseModel(HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(),
					e.getClass().toString(), e.getMessage()));
		}
	}

}
