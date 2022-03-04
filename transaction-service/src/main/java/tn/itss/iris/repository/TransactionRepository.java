package tn.itss.iris.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tn.itss.iris.model.Transaction;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, Long>{

}
