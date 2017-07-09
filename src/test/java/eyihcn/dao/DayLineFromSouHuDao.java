package eyihcn.dao;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import eyihcn.entity.DayLineFromSouHu;

@Repository("dayLineFromSouHuDao")
public class DayLineFromSouHuDao extends BaseMongoDao<DayLineFromSouHu, Integer> {

	public boolean checkExistsByDate(String date) {
		
		return super.checkExists(Criteria.where("date").is(date));
	}

	public boolean checkExistsByDateAndSharesCode(String date, String sharesCode) {
		return super.checkExists(Criteria.where("date").is(date).and("sharesCode"));
	}

}
