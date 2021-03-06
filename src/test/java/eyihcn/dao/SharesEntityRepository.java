package eyihcn.dao;

import org.springframework.stereotype.Repository;

import eyihcn.data.access.spring.data.mongodb.BaseMongoRepository;
import eyihcn.entity.SharesEntity;

@Repository("sharesEntityDao")
public interface SharesEntityRepository extends BaseMongoRepository<SharesEntity, Integer>,DerivedSharesEntityRepository{

}
