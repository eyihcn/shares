package eyihcn.shares;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import eyihcn.dao.DayLineFromSouHuDao;
import eyihcn.dao.SharesEntityDao;


public class PullDayLineClient {
	
	final Logger log = LoggerFactory.getLogger(PullDayLineClient.class);
	
	@Autowired
	@Qualifier("fireFoxSharesAPICallerByConnPool")
	FireFoxSharesAPICallerByConnPool fireFoxSharesAPICallerByConnPool;

	@Autowired
	@Qualifier("sharesEntityDao")
	SharesEntityDao sharesEntityDao;

	@Autowired
	@Qualifier("dayLineFromSouHuDao")
	DayLineFromSouHuDao dayLineFromSouHuDao;

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
				exe.execute(new UpdateDayLineDateTask(countDownLatch,fireFoxSharesAPICallerByConnPool, sharesEntityDao, dayLineFromSouHuDao, start, pageSize));
			}
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			exe.shutdown();
		}
		log.info("======== all done !");
	}
}