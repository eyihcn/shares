package eyihcn.shares;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.springframework.data.mongodb.core.query.Criteria;

import eyihcn.dao.DayLineFromSouHuDao;
import eyihcn.dao.SharesEntityDao;
import eyihcn.entity.SharesEntity;
import eyihcn.shares.utlis.DateUtils;

public class UpdateDayLineDateTask extends UpdateDayLineOperator implements Runnable {

	private SharesEntityDao sharesEntityDao;

	private String startDay = null;
	private String endDay = null;
	private int pageNumber;
	private int pageSize;

	CountDownLatch countDownLatch = null;

	public UpdateDayLineDateTask() {
		super();
	}

	public UpdateDayLineDateTask(FireFoxSharesAPICallerByConnPool fireFoxSharesAPICallerByConnPool, SharesEntityDao sharesEntityDao, DayLineFromSouHuDao dayLineFromSouHuDao, String startDay,
			String endDay, int pageNumber, int pageSize, CountDownLatch countDownLatch) {
		super(fireFoxSharesAPICallerByConnPool,dayLineFromSouHuDao);
		this.sharesEntityDao = sharesEntityDao;
		this.startDay = startDay;
		this.endDay = endDay;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.countDownLatch = countDownLatch;
	}
	
	public UpdateDayLineDateTask(FireFoxSharesAPICallerByConnPool fireFoxSharesAPICallerByConnPool, SharesEntityDao sharesEntityDao, DayLineFromSouHuDao dayLineFromSouHuDao, int pageNumber, int pageSize, CountDownLatch countDownLatch) {
		this(fireFoxSharesAPICallerByConnPool, sharesEntityDao, dayLineFromSouHuDao, "", DateUtils.formatDate(new Date()), pageNumber, pageSize, countDownLatch);
	}

	public void run() {
		try {
			List<SharesEntity> sharesEntityList = sharesEntityDao.find(new Criteria(), pageSize, pageNumber);
			super.pull(sharesEntityList, startDay, endDay);
			// 一个线程处理100只股票
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != this.countDownLatch) {
				this.countDownLatch.countDown();
			}
		}
	}
}
