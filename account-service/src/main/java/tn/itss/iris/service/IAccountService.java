package tn.itss.iris.service;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import tn.itss.iris.model.Account;
import tn.itss.iris.model.AccountRequest;

@Service
public interface IAccountService {

	public Account addAccount(Account account);
	public Boolean deleteAccount(Long id);
	public Account updateAccount(Account account);
	public Account getAccount(Long id);
	public Object getAllAccounts(Boolean pagination, Integer size, Integer page, String orderBy, String orderType, String accountName);
	public Object getAllAccounts(Boolean pagination, Integer size, Integer page, String orderBy, String orderType,
			Account account);
	public List<AccountRequest> listAccountsToListAccountRequests(Stream<Account> streams);
}
