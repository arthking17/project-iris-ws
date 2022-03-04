package tn.itss.iris.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tn.itss.iris.model.Account;

@Repository
public interface AccountRepository extends MongoRepository<Account, Long>{

}
