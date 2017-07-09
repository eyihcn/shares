package eyihcn.shares;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

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

	private int pageNumber;
	private int pageSize;

	public UpdateDayLineDateTask() {
		super();
	}

	public UpdateDayLineDateTask(FireFoxSharesAPICallerByConnPool fireFoxSharesAPICallerByConnPool, SharesEntityDao sharesEntityDao, DayLineFromSouHuDao dayLineFromSouHuDao, int pageNum, int pageSize) {
		super();
		this.fireFoxSharesAPICallerByConnPool = fireFoxSharesAPICallerByConnPool;
		this.sharesEntityDao = sharesEntityDao;
		this.dayLineFromSouHuDao = dayLineFromSouHuDao;
		this.pageNumber = pageNum;
		this.pageSize = pageSize;
	}

	public void run() {

		List<SharesEntity> sharesEntityList = sharesEntityDao.find(new Criteria(), pageSize, pageNumber);
		if (CollectionUtils.isEmpty(sharesEntityList)) {
			log.info("no data , pageSize : " + pageSize + " ,pageNumber : " + pageNumber);
			return;
		}
		Date today = new Date();
		String date = null;
		while (DateUtils.isWeekendDay(today)) {
			date = DateUtils.formatDate(today);
			log.info(date + " 深沪A股休市！");
			// 向前推一天
			today = DateUtils.decrementDay(today);
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
			Gson gson = new Gson();
			try {
				P:while (true) {
					// 每次查询最多100条数据
					startDate = getStartdateFromEnd(endDate);
					log.info("拉取目标："+sharesEntity.getSharesNameCn()+"("+sharesEntity.getSharesCode()+") ； 日期区间："+startDate +"  ~ "+endDate);

					String respStr = fireFoxSharesAPICallerByConnPool.request(sharesCode, startDate, endDate);
					String jsonStr = respStr.replace("historySearchHandler([", "").replace("])", "");
					HisHqEntity en = gson.fromJson(jsonStr, HisHqEntity.class);
					List<List<String>> hq = en.getHq();
					if (CollectionUtils.isEmpty(hq)) {
						log.info("拉取目标："+sharesEntity.getSharesNameCn()+"("+sharesEntity.getSharesCode()+") ； 日期区间："+startDate +"  ~ "+endDate+" ;拉取数据量为 0 ，结束");
						break;
					}
					/*
					 * 日期 0: 2017-07-07 ; 开盘价 1:38.30 ; 收盘价 2:39.33; 涨跌额 3:0.85;
					 * 涨跌幅 4: 2.21%; 最低 5: 38.09; 最高 6:39.49; 成交量(手) 7:36235;
					 * 成交金额(万)8 : 14097.93; 换手率 9: 14.49%
					 */
					DayLineFromSouHu dayLineFromSouHu = null;
					for (List<String> dayLineData : hq) {
						// 若当前日期 已有数据，结束拉取
						if (dayLineFromSouHuDao.checkExistsByDateAndSharesCode(dayLineData.get(0), sharesCode)) {
							log.info("拉取目标："+sharesEntity.getSharesNameCn()+"("+sharesEntity.getSharesCode()+") ； 日期区间："+startDate +"  ~ "+endDate+ "; 中断拉取 ，"+dayLineData.get(0) + "的股票数据已经拉取！");
							break P;
						}
						dayLineFromSouHu = new DayLineFromSouHu();
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
						dayLineFromSouHu.setTurnoverRate(Double.valueOf(dayLineData.get(9).replace("%", "")));
						dayLineFromSouHuDao.save(dayLineFromSouHu);
					}
					endDate = DateUtils.formatDate(DateUtils.decrementDay(DateUtils.parseDate(startDate)));
					log.info("拉取目标："+sharesEntity.getSharesNameCn()+"("+sharesEntity.getSharesCode()+") ； 日期区间："+startDate +"  ~ "+endDate+ "; 拉取成功！");
				}
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private float round(String doubleStr) {
		BigDecimal bigG=new BigDecimal(doubleStr).setScale(2, BigDecimal.ROUND_DOWN); //期望得到12.4
		return bigG.floatValue();
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

}
