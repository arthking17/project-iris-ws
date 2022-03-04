package tn.itss.iris.resource;

import java.text.ParseException;
import java.time.LocalDateTime;

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

import tn.itss.iris.model.Account;
import tn.itss.iris.model.AccountRequest;
import tn.itss.iris.model.ResponseModel;
import tn.itss.iris.service.IAccountService;

@RestController
@RequestMapping("/accounts")
public class AccountResource {

	@Autowired
	IAccountService accountService;

	private static final Logger l = LogManager.getLogger(IAccountService.class);

	/**
	 * @description retrieve all accounts from DB
	 * @author wi11i4m
	 * @return ResponseEntity
	 */
	@GetMapping
	public ResponseEntity<ResponseModel> getAllAccounts(
			@RequestParam(required = false, name = "pagination") Boolean pagination,
			@RequestParam(required = false, name = "size") Integer size,
			@RequestParam(required = false, name = "page") Integer page,
			@RequestParam(required = false, name = "orderBy") String orderBy,
			@RequestParam(required = false, name = "orderType") String orderType,
			@RequestParam(required = false, name = "accountName") String accountName,
			@RequestBody(required = false) AccountRequest accountRequest) {

		try {
			Object accounts = null;
			l.info(accountName);
			if (accountRequest == null)
				accounts = accountService.getAllAccounts(pagination, size, page, orderBy, orderType, accountName);
//			List<AccountRequest> accountResponses = accountService.listAccountsToListAccountRequests(streams.stream());
			else {
				accounts = accountService.getAllAccounts(pagination, size, page, orderBy, orderType,
						accountRequest.AccountRequestToAccount());
				l.info("get All Accounts By accountRequest - " + accountRequest);
			}
			if (accounts != null)
				return ResponseEntity
						.ok(new ResponseModel(HttpStatus.OK, LocalDateTime.now(), "Account Found", accounts));
			else
				return ResponseEntity
						.ok(new ResponseModel(HttpStatus.OK, LocalDateTime.now(), "Account Not Found", null));
		} catch (Exception e) {
			l.error(e);
			return ResponseEntity.ok(new ResponseModel(HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(),
					e.getClass().toString(), e.getMessage()));
		}
	}

	/**
	 * @description retrieve account by id
	 * @author wi11i4m
	 * @param id
	 * @return ResponseEntity
	 */
	@GetMapping("/{account_id}")
	public ResponseEntity<ResponseModel> getAccount(@PathVariable("account_id") Long id) {

		try {
			Account accountFound = accountService.getAccount(id);
			if (accountFound != null) {

				// formatting Object Account to AccountRequest
				AccountRequest accountResponse = new AccountRequest();
				accountResponse.AccountToAccountRequest(accountFound);

				return ResponseEntity
						.ok(new ResponseModel(HttpStatus.OK, LocalDateTime.now(), "Account Found", accountResponse));
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel(HttpStatus.BAD_REQUEST,
						LocalDateTime.now(), "Not Found!!!", "No Account with id " + id + " Found!"));
			}

		} catch (Exception e) {
			l.error(e);
			return ResponseEntity.ok(new ResponseModel(HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(),
					e.getClass().toString(), e.getMessage()));
		}
	}

	/**
	 * @description add new account to the DB
	 * @author wi11i4m
	 * @param AccountRequest
	 * @return ResponseEntity
	 * @throws ParseException
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<ResponseModel> addAccount(@Valid @RequestBody AccountRequest accountRequest)
			throws ParseException {

		try {
			Account account = accountRequest.AccountRequestToAccount();

			// Adding account to DB
			Account accountAdded = accountService.addAccount(account);
			l.debug("Account added : " + accountAdded);

			// formatting Object Account to AccountRequest
			AccountRequest accountResponse = new AccountRequest();
			accountResponse.AccountToAccountRequest(accountAdded);

			return ResponseEntity
					.ok(new ResponseModel(HttpStatus.CREATED, LocalDateTime.now(), "Account Added", accountResponse));
		} catch (Exception e) {
			l.error(e);
			return ResponseEntity.ok(new ResponseModel(HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(),
					e.getClass().toString(), e.getMessage()));
		}

	}

	/**
	 * @description update account
	 * @author wi11i4m
	 * @param AccountRequest
	 * @return ResponseEntity
	 * @throws ParseException
	 */
	@PutMapping
	public ResponseEntity<ResponseModel> updateAccount(@Valid @RequestBody AccountRequest accountRequest)
			throws ParseException {

		try {
			Account account = accountRequest.AccountRequestToAccount();
			l.debug("Updating account : " + account);

			Account accountUpdated = accountService.updateAccount(account);
			l.debug("Account updated : " + accountUpdated);

			// formatting Object Account to AccountRequest
			AccountRequest accountResponse = new AccountRequest();
			accountResponse.AccountToAccountRequest(accountUpdated);

			return ResponseEntity
					.ok(new ResponseModel(HttpStatus.OK, LocalDateTime.now(), "Account Updated", accountResponse));

		} catch (Exception e) {
			l.error(e);
			return ResponseEntity.ok(new ResponseModel(HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(),
					e.getClass().toString(), e.getMessage()));
		}
	}

	/**
	 * @description delete single account by id
	 * @author wi11i4m
	 * @param id
	 * @return ResponseEntity
	 */
	@DeleteMapping("/{account_id}")
	public ResponseEntity<ResponseModel> deleteAccount(@Valid @PathVariable("account_id") Long id) {

		try {
			Boolean deleteState = accountService.deleteAccount(id);
			if (deleteState)
				return ResponseEntity.ok(new ResponseModel(HttpStatus.OK, LocalDateTime.now(), "Account Deleted",
						"Account with id '" + id + "' deleted!"));
			else
				return ResponseEntity.ok(new ResponseModel(HttpStatus.BAD_REQUEST, LocalDateTime.now(),
						"Account Not Found", "Account with id '" + id + "' not found or already deleted!"));

		} catch (Exception e) {
			l.error(e);
			return ResponseEntity.ok(new ResponseModel(HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(),
					e.getClass().toString(), e.getMessage()));
		}
	}

}
