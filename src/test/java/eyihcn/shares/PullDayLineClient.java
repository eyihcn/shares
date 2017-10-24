package eyihcn.shares;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;

import com.google.common.collect.Lists;

import eyihcn.dao.DayLineFromSouHuRepository;
import eyihcn.dao.SharesEntityRepository;
import eyihcn.entity.SharesEntity;


public class PullDayLineClient {
	
	final Logger log = LoggerFactory.getLogger(PullDayLineClient.class);
	
	@Resource
	FireFoxSharesAPICallerByConnPool fireFoxSharesAPICallerByConnPool;

	@Resource
	SharesEntityRepository sharesEntityDao;

	@Resource
	DayLineFromSouHuRepository dayLineFromSouHuDao;

	public void pullAll(){
		long totalCount = sharesEntityDao.count();
		log.info(" 将深沪A股 拉取 "+totalCount+" 只股票的日线数据");
		ExecutorService exe = Executors.newFixedThreadPool(6);
		int pageSize = 100;
		int totalPageCount = (int) Math.ceil(((double)totalCount)/pageSize);
		log.info("每页 "+pageSize+" 条数据; 一共  "+totalPageCount+" 页");
		try {
			CountDownLatch countDownLatch = new CountDownLatch(totalPageCount);
			for (int start =1; start <= totalPageCount; start ++) {
				exe.execute(new UpdateDayLineDateTask(fireFoxSharesAPICallerByConnPool, sharesEntityDao, dayLineFromSouHuDao, start, pageSize,countDownLatch));
			}
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			exe.shutdown();
		}
		log.info("======== all done !");
	}
	
	public void pullAll(String startDate, String endDate, String ... sharesCodes){
		if (StringUtils.isBlank(startDate)) {
			startDate = "";
		}
		if (StringUtils.isBlank(endDate)) {
			endDate = "";
		}
		if (sharesCodes == null || sharesCodes.length == 0) {
			long totalCount = sharesEntityDao.count();
			log.info(" 将深沪A股 拉取 "+totalCount+" 只股票的日线数据");
			ExecutorService exe = Executors.newFixedThreadPool(6);
			int pageSize = 100;
			int totalPageCount = (int) Math.ceil(((double)totalCount)/pageSize);
			log.info("每页 "+pageSize+" 条数据; 一共  "+totalPageCount+" 页");
			try {
				CountDownLatch countDownLatch = new CountDownLatch(totalPageCount);
				for (int start =1; start <= totalPageCount; start ++) {
					exe.execute(new UpdateDayLineDateTask(fireFoxSharesAPICallerByConnPool, sharesEntityDao, dayLineFromSouHuDao, 
							startDate, endDate ,start, pageSize,countDownLatch));
				}
				countDownLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally{
				exe.shutdown();
			}
		}else {
			Page<SharesEntity> queryForPage = sharesEntityDao.queryForPage(Criteria.where("sharesCode").in(Lists.newArrayList(sharesCodes)),null);
			if (queryForPage !=null) {
				List<SharesEntity> sharesEntityList =queryForPage.getContent();
				if (CollectionUtils.isNotEmpty(sharesEntityList)) {
					UpdateDayLineOperator updateDayLineOperator = new UpdateDayLineOperator(fireFoxSharesAPICallerByConnPool,dayLineFromSouHuDao);
					updateDayLineOperator.pull(sharesEntityList, startDate, endDate);
				}
			}
		}
		log.info("======== all done !");
	}
}