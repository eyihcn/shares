package eyihcn.dao;

import org.springframework.stereotype.Repository;

import eyihcn.data.access.spring.data.mongodb.BaseMongoRepository;
import eyihcn.entity.DayLineFromSouHu;

@Repository("dayLineFromSouHuDao")
public interface DayLineFromSouHuRepository extends BaseMongoRepository<DayLineFromSouHu, Integer>, DerivedDayLineFromSouHuRepository{

}
