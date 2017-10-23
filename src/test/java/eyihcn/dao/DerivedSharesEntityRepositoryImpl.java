package eyihcn.dao;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

import eyihcn.entity.SharesEntity;

public class DerivedSharesEntityRepositoryImpl implements DerivedSharesEntityRepository{

	@Resource
	private MongoOperations mongoOperations;
	
	@Override
	public boolean checkExistsBySharesCode(String sharesCode) {
		
		Assert.notNull(sharesCode, "The given sharesCode can not be null");
		return mongoOperations.exists(new Query(Criteria.where("sharesCode").is(sharesCode)), SharesEntity.class);
	}

}
