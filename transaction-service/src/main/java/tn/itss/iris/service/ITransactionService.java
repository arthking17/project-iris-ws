package tn.itss.iris.service;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import tn.itss.iris.model.Transaction;
import tn.itss.iris.model.TransactionRequest;

@Service
public interface ITransactionService {

	public Transaction addTransaction(Transaction transaction);
	public Boolean deleteTransaction(Long id);
	public Transaction updateTransaction(Transaction transaction);
	public Transaction getTransaction(Long id);
	public Object getAllTransactions(Boolean pagination, Integer size, Integer page, String orderBy, String orderType, String currency);
	public Object getAllTransactions(Boolean pagination, Integer size, Integer page, String orderBy, String orderType,
			Transaction transaction);
	public List<TransactionRequest> listTransactionsToListTransactionRequests(Stream<Transaction> streams);

}
