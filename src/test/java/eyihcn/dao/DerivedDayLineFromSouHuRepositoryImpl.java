package eyihcn.dao;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import eyihcn.entity.DayLineFromSouHu;
public class DerivedDayLineFromSouHuRepositoryImpl implements DerivedDayLineFromSouHuRepository {

	@Resource
	private MongoOperations mongoOperations;
	
	@Override
	public boolean checkExistsByDate(String date) {
		
		return mongoOperations.exists(new Query(Criteria.where("date").is(date)), DayLineFromSouHu.class);
	}

	@Override
	public boolean checkExistsByDateAndSharesCode(String date, String sharesCode) {
		return mongoOperations.exists(new Query(Criteria.where("date").is(date).and("sharesCode").is(sharesCode)), DayLineFromSouHu.class);
	}
}
