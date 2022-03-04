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

import tn.itss.iris.model.Transaction;
import tn.itss.iris.model.TransactionRequest;
import tn.itss.iris.repository.TransactionRepository;

@Service
public class TransactionServiceImpl implements ITransactionService{

	@Autowired
	TransactionRepository transactionRepo;

	@Autowired
	SequenceGenerator sequenceGenerator;

	private static final Logger l = LogManager.getLogger(ITransactionService.class);

	@Override
	public Transaction addTransaction(Transaction transaction) {

		transaction.setTransactionId(sequenceGenerator.generateSequence(Transaction.SEQUENCE_NAME));

		l.info("Adding transaction : " + transaction);
		return transactionRepo.save(transaction);
	}

	@Override
	public Boolean deleteTransaction(Long id) {
		l.info("Deleting transaction : with id " + id);
		if (getTransaction(id) != null) {
			transactionRepo.deleteById(id);
			return true;
		} else
			return false;
	}

	@Override
	public Transaction updateTransaction(Transaction transaction) {
		transaction.setCreatedDate(getTransaction(transaction.getTransactionId()).getCreatedDate());
		l.info("Updating transaction : " + transaction);
		return transactionRepo.save(transaction);
	}

	@Override
	public Transaction getTransaction(Long id) {
		l.info("in  getTransaction id = " + id);
		Transaction transaction = transactionRepo.findById(id).orElse(null);
		l.info("transaction returned : " + transaction);
		return transaction;
	}

	@Override
	public Object getAllTransactions(Boolean pagination, Integer size, Integer page, String orderBy, String orderType,
			String currency) {
		
		//initialize value
		Supplier<Stream<Transaction>> transactions = null;
		Page<Transaction> transactionsPageable = null;
		if (orderType == null)
			orderType = "asc";

		/**
		 * PAGINATION < OFF >
		 */
		if (pagination == null || pagination == false) {
			transactions = () -> transactionRepo.findAll().stream();
			l.info(transactions.get().collect(toList()));
		}
		/**
		 * PAGINATION < ON >
		 */
		else if (pagination) {
			l.info(transactionRepo.findAll(Pageable.ofSize(3)).getPageable());
			if (size == null)
				transactionsPageable = transactionRepo.findAll(Pageable.ofSize(5));
			else if (size != null && page == null)
				transactionsPageable = transactionRepo.findAll(Pageable.ofSize(size));
			else if (size != null && page != null)
				transactionsPageable = transactionRepo.findAll(Pageable.ofSize(size).withPage(page));

			final List<Transaction> listTransactions = transactionsPageable.getContent();
			transactions = () -> listTransactions.stream();

		}
		
		// filter list of Transactions by Transaction Name
		if (currency != null) {
			transactions.get().filter(c -> c.getCurrency().equals(currency));
			l.info(currency);
		}

		/**
		 *  order By < Field >
		 */
		if (orderBy != null) {
			if (orderBy.equals("transactionDate") && orderType.equals("asc")) {
				transactions.get().sorted((c1, c2) -> c1.getTransactionDate().compareTo(c2.getTransactionDate()));
			} else if (orderBy.equals("transactionDate") && orderType.equals("desc")) {
				transactions.get().sorted((c1, c2) -> c2.getTransactionDate().compareTo(c1.getTransactionDate()));
			}
		}

		/**
		 * < paginate > if necessary and return the list
		 */
		if (pagination != null && pagination != false && transactionsPageable != null) {
			Map<String, Object> transactionsPaginate = new HashMap<>();
			transactionsPaginate.put("content", listTransactionsToListTransactionRequests(transactions.get()));
			transactionsPaginate.put("pageable", transactionsPageable.getPageable());
			transactionsPaginate.put("totalElements", transactionsPageable.getTotalElements());
			transactionsPaginate.put("totalPages", transactionsPageable.getTotalElements());
			transactionsPaginate.put("last", transactionsPageable.isLast());
			transactionsPaginate.put("size", transactionsPageable.getSize());
			transactionsPaginate.put("number", transactionsPageable.getNumber());
			transactionsPaginate.put("sort", transactionsPageable.getSort());
			transactionsPaginate.put("numberOfElements", transactionsPageable.getNumberOfElements());
			transactionsPaginate.put("first", transactionsPageable.isFirst());
			transactionsPaginate.put("empty", transactionsPageable.isEmpty());
			return transactionsPaginate;
		} else
			return listTransactionsToListTransactionRequests(transactions.get());
	}

	@Override
	public Object getAllTransactions(Boolean pagination, Integer size, Integer page, String orderBy, String orderType,
			Transaction transaction) {
		
		//initialize value
		Supplier<Stream<Transaction>> transactions = null;
		Page<Transaction> transactionsPageable = null;
		if (orderType == null)
			orderType = "asc";

		/**
		 * PAGINATION < OFF >
		 */
		if (pagination == null || pagination == false) {
			transactions = () -> transactionRepo
					.findAll(Example.of(transaction, ExampleMatcher.matchingAny().withIgnoreCase().withIgnoreNullValues()))
					.stream();
			l.info(transactions.get().collect(toList()));
		}
		/**
		 * PAGINATION < ON >
		 */
		else if (pagination) {
			l.info(transactionRepo.findAll(Pageable.ofSize(3)).getPageable());
			if (size == null)
				transactionsPageable = transactionRepo.findAll(
						Example.of(transaction, ExampleMatcher.matchingAny().withIgnoreCase().withIgnoreNullValues()),
						Pageable.ofSize(5));
			else if (size != null && page == null)
				transactionsPageable = transactionRepo.findAll(
						Example.of(transaction, ExampleMatcher.matchingAny().withIgnoreCase().withIgnoreNullValues()),
						Pageable.ofSize(size));
			else if (size != null && page != null)
				transactionsPageable = transactionRepo.findAll(
						Example.of(transaction, ExampleMatcher.matchingAny().withIgnoreCase().withIgnoreNullValues()),
						Pageable.ofSize(size).withPage(page));

			final List<Transaction> listTransactions = transactionsPageable.getContent();
			transactions = () -> listTransactions.stream();
		}

		/**
		 *  order By < Field >
		 */
		if (orderBy != null) {
			if (orderBy.equals("transactionDate") && orderType.equals("asc")) {
				transactions.get().sorted((c1, c2) -> c1.getTransactionDate().compareTo(c2.getTransactionDate()));
			} else if (orderBy.equals("transactionDate") && orderType.equals("desc")) {
				transactions.get().sorted((c1, c2) -> c2.getTransactionDate().compareTo(c1.getTransactionDate()));
			}
		}

		/**
		 * < paginate > if necessary and return the list
		 */
		if (pagination != null && pagination != false && transactionsPageable != null) {
			Map<String, Object> transactionsPaginate = new HashMap<>();
			transactionsPaginate.put("content", listTransactionsToListTransactionRequests(transactions.get()));
			transactionsPaginate.put("pageable", transactionsPageable.getPageable());
			transactionsPaginate.put("totalElements", transactionsPageable.getTotalElements());
			transactionsPaginate.put("totalPages", transactionsPageable.getTotalElements());
			transactionsPaginate.put("last", transactionsPageable.isLast());
			transactionsPaginate.put("size", transactionsPageable.getSize());
			transactionsPaginate.put("number", transactionsPageable.getNumber());
			transactionsPaginate.put("sort", transactionsPageable.getSort());
			transactionsPaginate.put("numberOfElements", transactionsPageable.getNumberOfElements());
			transactionsPaginate.put("first", transactionsPageable.isFirst());
			transactionsPaginate.put("empty", transactionsPageable.isEmpty());
			return transactionsPaginate;
		} else
			return listTransactionsToListTransactionRequests(transactions.get());
	}

	@Override
	public List<TransactionRequest> listTransactionsToListTransactionRequests(Stream<Transaction> streams) {
		if (streams != null) {
			ArrayList<TransactionRequest> transactionResponses = new ArrayList<TransactionRequest>();
			streams.forEach(transaction -> {
				TransactionRequest transactionResponse = new TransactionRequest();
				// formatting Object Transaction to TransactionRequest
				transactionResponse.TransactionToTransactionRequest(transaction);
				l.info(transactionResponse);
				transactionResponses.add(transactionResponse);
			});
			return transactionResponses;
		} else
			return null;
	}

}
