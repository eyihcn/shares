package eyihcn.shares;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import eyihcn.dao.DayLineFromSouHuRepository;
import eyihcn.dao.SharesEntityRepository;
import eyihcn.entity.SharesEntity;
import eyihcn.shares.utlis.DateUtils;

public class UpdateDayLineDateTask extends UpdateDayLineOperator implements Runnable {

	private SharesEntityRepository sharesEntityDao;

	private String startDay = null;
	private String endDay = null;
	private int pageNumber;
	private int pageSize;

	CountDownLatch countDownLatch = null;

	public UpdateDayLineDateTask() {
		super();
	}

	public UpdateDayLineDateTask(FireFoxSharesAPICallerByConnPool fireFoxSharesAPICallerByConnPool, SharesEntityRepository sharesEntityDao, DayLineFromSouHuRepository dayLineFromSouHuDao, String startDay,
			String endDay, int pageNumber, int pageSize, CountDownLatch countDownLatch) {
		super(fireFoxSharesAPICallerByConnPool,dayLineFromSouHuDao);
		this.sharesEntityDao = sharesEntityDao;
		this.startDay = startDay;
		this.endDay = endDay;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.countDownLatch = countDownLatch;
	}
	
	public UpdateDayLineDateTask(FireFoxSharesAPICallerByConnPool fireFoxSharesAPICallerByConnPool, SharesEntityRepository sharesEntityDao, DayLineFromSouHuRepository dayLineFromSouHuDao, int pageNumber, int pageSize, CountDownLatch countDownLatch) {
		this(fireFoxSharesAPICallerByConnPool, sharesEntityDao, dayLineFromSouHuDao, "", DateUtils.formatDate(new Date()), pageNumber, pageSize, countDownLatch);
	}

	@Override
	public void run() {
		try {
			Pageable pageable = PageRequest.of(pageNumber, pageNumber);
			Page<SharesEntity> onePage = sharesEntityDao.findAll(pageable);
			if (null != onePage) {
				super.pull(onePage.getContent(), startDay, endDay);
			}
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
