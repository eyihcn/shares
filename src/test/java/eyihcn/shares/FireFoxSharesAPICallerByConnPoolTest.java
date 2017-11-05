package eyihcn.shares;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eyihcn.dao.DayLineFromSouHuRepository;
import eyihcn.dao.SharesEntityRepository;
import eyihcn.entity.SharesEntity;
import eyihcn.shares.utlis.DateUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContext-test.xml", "classpath:/spring-data-mongdb.xml" })
public class FireFoxSharesAPICallerByConnPoolTest {

	final Logger log = LoggerFactory.getLogger(FireFoxSharesAPICallerByConnPoolTest.class);

	@Autowired
	@Qualifier("fireFoxSharesAPICallerByConnPool")
	FireFoxSharesAPICallerByConnPool fireFoxSharesAPICallerByConnPool;

	@Autowired
	@Qualifier("sharesEntityDao")
	SharesEntityRepository sharesEntityDao;

	@Autowired
	@Qualifier("dayLineFromSouHuDao")
	DayLineFromSouHuRepository dayLineFromSouHuDao;

	@Autowired
	@Qualifier("pullDayLineClient")
	PullDayLineClient pullDayLineClient;

	long start;

	@Before
	public void setup() {
		log.info("=========================== setup ~");
		start = System.currentTimeMillis();
	}

	@After
	public void teardown() {
		log.info("~~~~~~~~~~ test cost time ms : " + (System.currentTimeMillis() - start));
		log.info("=========================== teardown ~");
	}
	
	
	@Test
	public void querySouHuBySharesCodeAll() {
		pullDayLineClient.pullAll("2017-01-01", "",null);
	}
	
	@Test
	public void querySouHuBySharesCode() {
//		pullDayLineClient.pullAll("2017-07-01", "2017-07-30", "000002");
		pullDayLineClient.pullAll(Arrays.asList("000728"));
		
//		pullDayLineClient.pullAll("2017-07-01", "", "002848");
	}


	@Test
	public void testExportExecel() {
		String absPath = "C:\\Users\\lenovo\\Desktop\\2017-10-29-12-40_rankash.xls";
		FileInputStream in = null;
		try {
			in = new FileInputStream(absPath);
			Workbook wb = ExcelUtils.newWorkbook(in, absPath);
			Sheet sheet = wb.getSheetAt(0);
			insertSharesEntity(sheet);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void insertSharesEntity(Sheet sheet) {
		// 代码 名称 最新价 涨跌幅 涨跌额 买入 卖出 成交量 成交额 今开 昨收 最高 最低
		Iterator<Row> iterRow = sheet.iterator();
		iterRow.next();
		iterRow.next();
		int count = 1;
		for (; iterRow.hasNext();) {
			System.out.println(count++);
			Row next = iterRow.next();
			SharesEntity sharesEntity = new SharesEntity();
			// 代码
			String sharesCode = ExcelUtils.getCellValue(next, 0).replaceAll("sz", "").replaceAll("sh", "");
			sharesEntity.setSharesCode(sharesCode);
			System.out.println(sharesCode);
			// 中文名称
			String sharesNameCn = ExcelUtils.getCellValue(next, 1);
			sharesEntity.setSharesNameCn(sharesNameCn);
			if (!sharesEntityDao.checkExistsBySharesCode(sharesCode)) {
				sharesEntityDao.save(sharesEntity);
			}
		}
	}

	
	@Test
	public void testQuery() {
		String respStr = fireFoxSharesAPICallerByConnPool.request("000958","1999-07-05","1999-11-21");
		System.out.println(respStr);
		System.out.println(DateUtils.formatDate(new Date()));
//		String jsonStr = respStr.replace("historySearchHandler([", "").replace("])", "");
//		System.out.println(jsonStr);
//		Gson gson = new Gson();
//		HisHqEntity en = gson.fromJson(jsonStr, HisHqEntity.class);
//		System.out.println(en.getCode());
	}

}
