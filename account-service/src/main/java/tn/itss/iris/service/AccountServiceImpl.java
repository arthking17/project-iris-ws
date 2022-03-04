package tn.itss.iris.service;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import tn.itss.iris.model.Account;
import tn.itss.iris.model.AccountRequest;
import tn.itss.iris.repository.AccountRepository;

@Service
public class AccountServiceImpl implements IAccountService {

	@Autowired
	AccountRepository accountRepo;

	@Autowired
	SequenceGenerator sequenceGenerator;

	private static final Logger l = LogManager.getLogger(IAccountService.class);

	@Override
	public Account addAccount(Account account) {

		account.setAccountId(sequenceGenerator.generateSequence(Account.SEQUENCE_NAME));

		l.info("Adding account : " + account);
		return accountRepo.save(account);
	}

	@Override
	public Boolean deleteAccount(Long id) {
		l.info("Deleting account : with id " + id);
		if (getAccount(id) != null) {
			accountRepo.deleteById(id);
			return true;
		} else
			return false;
	}

	@Override
	public Account updateAccount(Account account) {
		l.info("Updating account : " + account);
		return accountRepo.save(account);
	}

	@Override
	public Account getAccount(Long id) {
		l.info("in  getAccount id = " + id);
		Account account = accountRepo.findById(id).orElse(null);
		l.info("account returned : " + account);
		return account;
	}

	@Override
	public Object getAllAccounts(Boolean pagination, Integer size, Integer page, String orderBy, String orderType,
			String accountName) {
		
		//initialize value
		Supplier<Stream<Account>> accounts = null;
		Page<Account> accountsPageable = null;
		if (orderType == null)
			orderType = "asc";

		/**
		 * PAGINATION < OFF >
		 */
		if (pagination == null || pagination == false) {
			accounts = () -> accountRepo.findAll().stream();
			l.info(accounts.get().collect(toList()));
		}
		/**
		 * PAGINATION < ON >
		 */
		else if (pagination) {
			l.info(accountRepo.findAll(Pageable.ofSize(3)).getPageable());
			if (size == null)
				accountsPageable = accountRepo.findAll(Pageable.ofSize(5));
			else if (size != null && page == null)
				accountsPageable = accountRepo.findAll(Pageable.ofSize(size));
			else if (size != null && page != null)
				accountsPageable = accountRepo.findAll(Pageable.ofSize(size).withPage(page));

			final List<Account> listAccounts = accountsPageable.getContent();
			accounts = () -> listAccounts.stream();

		}
		
		// filter list of Accounts by Account Name
		if (accountName != null) {
			accounts.get().filter(c -> c.getAccountName().contains(accountName));
			l.info(accountName);
		}

		/**
		 *  order By < Field >
		 */
		if (orderBy != null) {
			if (orderBy.equals("accountName") && orderType.equals("asc")) {
				accounts.get().sorted((c1, c2) -> c1.getAccountName().compareToIgnoreCase(c2.getAccountName()));
			} else if (orderBy.equals("accountName") && orderType.equals("desc")) {
				accounts.get().sorted((c1, c2) -> c2.getAccountName().compareToIgnoreCase(c1.getAccountName()));
			}
		}

		/**
		 * < paginate > if necessary and return the list
		 */
		if (pagination != null && pagination != false && accountsPageable != null) {
			Map<String, Object> accountsPaginate = new HashMap<>();
			accountsPaginate.put("content", listAccountsToListAccountRequests(accounts.get()));
			accountsPaginate.put("pageable", accountsPageable.getPageable());
			accountsPaginate.put("totalElements", accountsPageable.getTotalElements());
			accountsPaginate.put("totalPages", accountsPageable.getTotalElements());
			accountsPaginate.put("last", accountsPageable.isLast());
			accountsPaginate.put("size", accountsPageable.getSize());
			accountsPaginate.put("number", accountsPageable.getNumber());
			accountsPaginate.put("sort", accountsPageable.getSort());
			accountsPaginate.put("numberOfElements", accountsPageable.getNumberOfElements());
			accountsPaginate.put("first", accountsPageable.isFirst());
			accountsPaginate.put("empty", accountsPageable.isEmpty());
			return accountsPaginate;
		} else
			return listAccountsToListAccountRequests(accounts.get());
	}

	@Override
	public Object getAllAccounts(Boolean pagination, Integer size, Integer page, String orderBy, String orderType,
			Account account) {
		
		//initialize value
		Supplier<Stream<Account>> accounts = null;
		Page<Account> accountsPageable = null;
		if (orderType == null)
			orderType = "asc";

		/**
		 * PAGINATION < OFF >
		 */
		if (pagination == null || pagination == false) {
			accounts = () -> accountRepo
					.findAll(Example.of(account, ExampleMatcher.matchingAny().withIgnoreCase().withIgnoreNullValues()))
					.stream();
			l.info(accounts.get().collect(toList()));
		}
		/**
		 * PAGINATION < ON >
		 */
		else if (pagination) {
			l.info(accountRepo.findAll(Pageable.ofSize(3)).getPageable());
			if (size == null)
				accountsPageable = accountRepo.findAll(
						Example.of(account, ExampleMatcher.matchingAny().withIgnoreCase().withIgnoreNullValues()),
						Pageable.ofSize(5));
			else if (size != null && page == null)
				accountsPageable = accountRepo.findAll(
						Example.of(account, ExampleMatcher.matchingAny().withIgnoreCase().withIgnoreNullValues()),
						Pageable.ofSize(size));
			else if (size != null && page != null)
				accountsPageable = accountRepo.findAll(
						Example.of(account, ExampleMatcher.matchingAny().withIgnoreCase().withIgnoreNullValues()),
						Pageable.ofSize(size).withPage(page));

			final List<Account> listAccounts = accountsPageable.getContent();
			accounts = () -> listAccounts.stream();
		}

		/**
		 *  order By < Field >
		 */
		if (orderBy != null) {
			if (orderBy.equals("accountName") && orderType.equals("asc")) {
				accounts.get().sorted((c1, c2) -> c1.getAccountName().compareToIgnoreCase(c2.getAccountName()));
			} else if (orderBy.equals("accountName") && orderType.equals("desc")) {
				accounts.get().sorted((c1, c2) -> c2.getAccountName().compareToIgnoreCase(c1.getAccountName()));
			}
		}

		/**
		 * < paginate > if necessary and return the list
		 */
		if (pagination != null && pagination != false && accountsPageable != null) {
			Map<String, Object> accountsPaginate = new HashMap<>();
			accountsPaginate.put("content", listAccountsToListAccountRequests(accounts.get()));
			accountsPaginate.put("pageable", accountsPageable.getPageable());
			accountsPaginate.put("totalElements", accountsPageable.getTotalElements());
			accountsPaginate.put("totalPages", accountsPageable.getTotalElements());
			accountsPaginate.put("last", accountsPageable.isLast());
			accountsPaginate.put("size", accountsPageable.getSize());
			accountsPaginate.put("number", accountsPageable.getNumber());
			accountsPaginate.put("sort", accountsPageable.getSort());
			accountsPaginate.put("numberOfElements", accountsPageable.getNumberOfElements());
			accountsPaginate.put("first", accountsPageable.isFirst());
			accountsPaginate.put("empty", accountsPageable.isEmpty());
			return accountsPaginate;
		} else
			return listAccountsToListAccountRequests(accounts.get());
	}

	@Override
	public List<AccountRequest> listAccountsToListAccountRequests(Stream<Account> streams) {
		if (streams != null) {
			ArrayList<AccountRequest> accountResponses = new ArrayList<AccountRequest>();
			streams.forEach(account -> {
				AccountRequest accountResponse = new AccountRequest();
				// formatting Object Account to AccountRequest
				accountResponse.AccountToAccountRequest(account);
				l.info(accountResponse);
				accountResponses.add(accountResponse);
			});
			return accountResponses;
		} else
			return null;
	}

}
