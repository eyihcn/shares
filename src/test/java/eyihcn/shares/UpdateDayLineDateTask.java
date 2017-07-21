package eyihcn.shares;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;

import com.google.gson.Gson;

import eyihcn.dao.DayLineFromSouHuDao;
import eyihcn.dao.SharesEntityDao;
import eyihcn.entity.DayLineFromSouHu;
import eyihcn.entity.HisHqEntity;
import eyihcn.entity.SharesEntity;
import eyihcn.shares.utlis.DateUtils;

public class UpdateDayLineDateTask implements Runnable {

	final Logger log = LoggerFactory.getLogger(this.getClass());

	private FireFoxSharesAPICallerByConnPool fireFoxSharesAPICallerByConnPool;
	private SharesEntityDao sharesEntityDao;
	private DayLineFromSouHuDao dayLineFromSouHuDao;

	private String startDay = null;
	private String endDay = null;
	private int pageNumber;
	private int pageSize;

	private Gson gson = new Gson();

	CountDownLatch countDownLatch = null;

	public UpdateDayLineDateTask() {
		super();
	}

	public UpdateDayLineDateTask(CountDownLatch countDownLatch, FireFoxSharesAPICallerByConnPool fireFoxSharesAPICallerByConnPool, SharesEntityDao sharesEntityDao,
			DayLineFromSouHuDao dayLineFromSouHuDao, int pageNumber, int pageSize) {
		this(fireFoxSharesAPICallerByConnPool, sharesEntityDao, dayLineFromSouHuDao, "", DateUtils.formatDate(new Date()), pageNumber, pageSize, countDownLatch);
		// 不传入起始时间
	}

	public UpdateDayLineDateTask(FireFoxSharesAPICallerByConnPool fireFoxSharesAPICallerByConnPool, SharesEntityDao sharesEntityDao, DayLineFromSouHuDao dayLineFromSouHuDao, String startDay,
			String endDay, int pageNumber, int pageSize, CountDownLatch countDownLatch) {
		super();
		this.fireFoxSharesAPICallerByConnPool = fireFoxSharesAPICallerByConnPool;
		this.sharesEntityDao = sharesEntityDao;
		this.dayLineFromSouHuDao = dayLineFromSouHuDao;
		this.startDay = startDay;
		this.endDay = endDay;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.countDownLatch = countDownLatch;
	}

	public void run() {
		try {
			List<SharesEntity> sharesEntityList = sharesEntityDao.find(new Criteria(), pageSize, pageNumber);
			if (CollectionUtils.isEmpty(sharesEntityList)) {
				log.info("no data , pageSize : " + pageSize + " ,pageNumber : " + pageNumber);
				return;
			}
			Date today = new Date();
			String date = null;
			while (DateUtils.isWeekendDay(today)) {
				date = DateUtils.formatDate(today);
				if (!isEqualOrBetween(date)) {
					log.info("日期 : " + date + " 不在限定日期区间范围之内：" + this.startDay + "  ~ " + this.endDay);
					return;
				}
				log.info(date + " 深沪A股休市！");
				// 向前推一天
				today = DateUtils.decrementDay(today);
			}
			date = DateUtils.formatDate(today);
			if (!isEqualOrBetween(date)) {
				log.info("日期 : " + date + " 不在限定日期区间范围之内：" + this.startDay + "  ~ " + this.endDay);
				return;
			}
			// 一个线程处理100只股票
			for (SharesEntity sharesEntity : sharesEntityList) {
				String sharesCode = sharesEntity.getSharesCode();
				if (StringUtils.isBlank(sharesCode)) {
					log.info("error : 股票代码为空！" + sharesEntity.getClass() + " id=" + sharesEntity.getId());
					continue;
				}
				// 检查当前的最新数据
				if (dayLineFromSouHuDao.checkExistsByDateAndSharesCode(date, sharesCode)) {
					log.info(sharesEntity.getSharesNameCn() + "[" + sharesCode + "]" + date + "的股票数据已经拉取！");
					return;
				}

				String startDate = null;
				String endDate = new String(date);

				P: while (true) {
					// 每次查询最多100条数据
					startDate = getStartdateFromEnd(endDate);
					if (StringUtils.isNotBlank(this.startDay)) {
						if (this.startDay.compareTo(endDate) > 0) {
							log.info("待抓取日期区间 : " + startDate + "  ~ " + endDate + " 不在限定日期区间范围之内：" + this.startDay + "  ~ " + this.endDay);
							return;
						} else {
							if (this.startDay.compareTo(startDate) >= 0) {

							}
						}
						if (StringUtils.isNotBlank(this.endDay)) {
							if (this.startDay.compareTo(startDate) > 0) {

							}
						} else {

						}
					} else {

					}
					if (StringUtils.isNotBlank(this.endDay)) {
						if (this.endDay.compareTo(startDate) < 0) {
							log.info("待抓去日期区间 : " + startDate + "  ~ " + endDate + " 不在限定日期区间范围之内：" + this.startDay + "  ~ " + this.endDay);
							return;
						}
					}
					// TODO ｓｔａｒｔ。ｅｎｄ

					List<List<String>> hq = getDayLineFromSouHu(sharesEntity, startDate, endDate);
					if (CollectionUtils.isEmpty(hq)) {
						log.info("拉取目标：" + sharesEntity.getSharesNameCn() + "(" + sharesCode + ") ； 日期区间：" + startDate + "  ~ " + endDate + " ;拉取数据量为 0 ，结束");
						break;
					}
					/*
					 * 日期 0: 2017-07-07 ; 开盘价 1:38.30 ; 收盘价 2:39.33; 涨跌额 3:0.85;
					 * 涨跌幅 4: 2.21%; 最低 5: 38.09; 最高 6:39.49; 成交量(手) 7:36235;
					 * 成交金额(万)8 : 14097.93; 换手率 9: 14.49%
					 */
					for (List<String> dayLineData : hq) {
						String tempDate = dayLineData.get(0);
						// 若当前日期 已有数据，结束拉取
						if (dayLineFromSouHuDao.checkExistsByDateAndSharesCode(tempDate, sharesCode)) {
							log.info("拉取目标：" + sharesEntity.getSharesNameCn() + "(" + sharesCode + ") ； 日期区间：" + startDate + "  ~ " + endDate + "; 中断拉取 ，" + tempDate + "的股票数据已经拉取！");
							break P;
						}
						try {
							saveDayLineEntity(sharesCode, dayLineData);
						} catch (Exception e) {
							e.printStackTrace();
							log.info("++++++saveDayLineEntity++++++ sharesCode : " + sharesCode + " 日期区间：" + startDate + "  ~ " + endDate);
						}
					}
					log.info("拉取目标：" + sharesEntity.getSharesNameCn() + "(" + sharesCode + ") ； 日期区间：" + startDate + "  ~ " + endDate + "; 拉取成功！ 共拉取日线数据： " + hq.size());
					endDate = DateUtils.formatDate(DateUtils.decrementDay(DateUtils.parseDate(startDate)));
					// log.info(Thread.currentThread().getName() + " 当前线程sleep 2
					// 秒");
					TimeUnit.SECONDS.sleep(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != this.countDownLatch) {
				this.countDownLatch.countDown();
			}
		}
	}

	private DayLineFromSouHu saveDayLineEntity(String sharesCode, List<String> dayLineData) {

		DayLineFromSouHu dayLineFromSouHu = new DayLineFromSouHu();
		dayLineFromSouHu.setSharesCode(sharesCode);
		dayLineFromSouHu.setDate(dayLineData.get(0));
		dayLineFromSouHu.setOpeningPrice(Double.valueOf(dayLineData.get(1)));
		dayLineFromSouHu.setClosingPrice(Double.valueOf(dayLineData.get(2)));
		dayLineFromSouHu.setFluctuation(Double.valueOf(dayLineData.get(3)));
		dayLineFromSouHu.setFluctuationPercent(Double.valueOf(dayLineData.get(4).replace("%", "")));
		dayLineFromSouHu.setLowestPrice(Double.valueOf(dayLineData.get(5)));
		dayLineFromSouHu.setHighestPrice(Double.valueOf(dayLineData.get(6)));
		dayLineFromSouHu.setVolume(Integer.valueOf(dayLineData.get(7)));
		dayLineFromSouHu.setTradedAmount(Double.valueOf(dayLineData.get(8)));
		String turnoverRate = dayLineData.get(9);
		if ("-".equals(turnoverRate)) {
			dayLineFromSouHu.setTurnoverRate(0);
		} else {
			dayLineFromSouHu.setTurnoverRate(Double.valueOf(turnoverRate.replace("%", "")));
		}
		dayLineFromSouHuDao.save(dayLineFromSouHu);
		return dayLineFromSouHu;
	}

	private List<List<String>> getDayLineFromSouHu(SharesEntity sharesEntity, String startDate, String endDate) {

		String sharesCode = sharesEntity.getSharesCode();
		log.info("拉取目标：" + sharesEntity.getSharesNameCn() + "(" + sharesCode + ") ； 日期区间：" + startDate + "  ~ " + endDate);
		String respStr = null;
		try {
			respStr = fireFoxSharesAPICallerByConnPool.request(sharesCode, startDate, endDate);
		} catch (Exception e1) {
			e1.printStackTrace();
			log.info("######getDayLineFromSouHu###### sharesCode : " + sharesCode + " 日期区间：" + startDate + "  ~ " + endDate);
			return Collections.emptyList();
		}
		if ("historySearchHandler({})\n".equals(respStr)) {
			log.info("拉取目标：" + sharesEntity.getSharesNameCn() + "(" + sharesCode + ") ； 日期区间：" + startDate + "  ~ " + endDate + " ; 查询无数据,停止拉取");
			return Collections.emptyList();
		}
		String jsonStr = respStr.replace("historySearchHandler([", "").replace("])", "");
		HisHqEntity en = gson.fromJson(jsonStr, HisHqEntity.class);
		return en.getHq();
	}

	private String getStartdateFromEnd(String endDate) throws ParseException {
		int count = 0;
		Date parseDate = DateUtils.parseDate(endDate);
		while (true) {
			if (count == 100) {
				break;
			}
			parseDate = DateUtils.decrementDay(parseDate);
			if (DateUtils.isWeekendDay(parseDate)) {
				continue;
			}
			count++;
		}
		return DateUtils.formatDate(parseDate);
	}

	private boolean isEqualOrBetween(String date) {

		if (StringUtils.isBlank(this.startDay) && StringUtils.isBlank(this.endDay)) {
			return true;
		}
		if (StringUtils.isNotBlank(this.startDay) && this.startDay.compareTo(date) > 0) {
			return false;
		}
		if (StringUtils.isNotBlank(this.endDay) && this.endDay.compareTo(date) < 0) {
			return false;
		}
		return true;
	}
}
