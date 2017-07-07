package eyihcn.dao;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import eyihcn.entity.SharesEntity;

@Repository("sharesEntityDao")
public class SharesEntityDao extends BaseMongoDao<SharesEntity, Integer> {

	public boolean checkExistsBySharesCode(String sharesCode) {
		if (StringUtils.isBlank(sharesCode)) {
			return false;
		}
		return super.checkExists(Criteria.where("sharesCode").is(sharesCode));
	}

}
