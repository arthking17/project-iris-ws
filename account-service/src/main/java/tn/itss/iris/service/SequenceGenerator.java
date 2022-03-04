package tn.itss.iris.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import tn.itss.iris.model.DatabaseSequence;

@Service
public class SequenceGenerator {
	@Autowired
	private MongoOperations mongoOperations;

	public long generateSequence(String seqName) {
		
		DatabaseSequence counter = mongoOperations.findAndModify(
				
				new Query(Criteria.where("_id").is(seqName)),
				new Update().inc("seq", 1), 
				FindAndModifyOptions.options().returnNew(true).upsert(true),
				DatabaseSequence.class);
		
		return !Objects.isNull(counter) ? counter.getSeq() : 1;
	}
}
