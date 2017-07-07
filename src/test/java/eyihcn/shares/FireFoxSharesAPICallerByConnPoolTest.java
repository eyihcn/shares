package eyihcn.shares;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.google.gson.Gson;

import eyihcn.dao.SharesEntityDao;
import eyihcn.entity.HisHqEntity;
import eyihcn.entity.SharesEntity;

@ContextConfiguration(locations = { "classpath:/applicationContext-test.xml", "classpath:/mongodb.xml" })
public class FireFoxSharesAPICallerByConnPoolTest extends AbstractJUnit4SpringContextTests {

	final Logger log = LoggerFactory.getLogger(FireFoxSharesAPICallerByConnPoolTest.class);

	@Autowired
	@Qualifier("fireFoxSharesAPICallerByConnPool")
	FireFoxSharesAPICallerByConnPool fireFoxSharesAPICallerByConnPool;

	@Autowired
	@Qualifier("sharesEntityDao")
	SharesEntityDao sharesEntityDao;

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
	public void test2() {

		String respStr = fireFoxSharesAPICallerByConnPool.request();
		String jsonStr = respStr.replace("historySearchHandler([", "").replace("])", "");
		Gson gson = new Gson();
		HisHqEntity en = gson.fromJson(jsonStr, HisHqEntity.class);
		System.out.println(en.getCode());
	}

	public void querySouHuBySharesCode(String code) {

	}

	@Test
	public void testExportExecel() {
		String absPath = "C:\\Users\\Administrator\\Desktop\\2017-07-05-18-25_沪.xls";
		FileInputStream in = null;
		try {
			in = new FileInputStream(absPath);
			Workbook wb = newWorkbook(in, absPath);
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
			String sharesCode = getCellValue(next, 0).replace("sz", "").replaceAll("sh", "");
			sharesEntity.setSharesCode(sharesCode);
			System.out.println(sharesCode);
			// 中文名称
			String sharesNameCn = getCellValue(next, 1);
			sharesEntity.setSharesNameCn(sharesNameCn);
			if (!sharesEntityDao.checkExistsBySharesCode(sharesCode)) {
				sharesEntityDao.save(sharesEntity);
			}
		}
	}

	private String getCellValue(Row row, int i) {

		Cell cell = row.getCell(i);
		Object cellVal = "";
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BOOLEAN:
			// 得到Boolean对象的方法
			cellVal = cell.getBooleanCellValue();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			// 先看是否是日期格式
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				// 读取日期格式
				cellVal = cell.getDateCellValue();
			} else {
				// 读取数字
				cellVal = cell.getNumericCellValue();
			}
			break;
		case Cell.CELL_TYPE_FORMULA:
			// 读取公式
			cellVal = cell.getCellFormula();
			break;
		case Cell.CELL_TYPE_STRING:
			// 读取String
			cellVal = cell.getRichStringCellValue().toString();
			break;
		}
		return cellVal.toString();
	}

	private Workbook newWorkbook(FileInputStream fileInputStream, String absPath) throws IOException {

		Workbook wb = null;
		if (absPath.endsWith(".xls")) {
			// 07之前版本
			wb = new HSSFWorkbook(fileInputStream);
		} else if (absPath.endsWith(".xlsx")) {
			// 07之后版本
			wb = new XSSFWorkbook(fileInputStream);
		}
		return wb;
	}

	@Test
	public void testQuery() {
		String respStr = fireFoxSharesAPICallerByConnPool.request();
		String jsonStr = respStr.replace("historySearchHandler([", "").replace("])", "");
		Gson gson = new Gson();
		HisHqEntity en = gson.fromJson(jsonStr, HisHqEntity.class);
		System.out.println(en.getCode());
	}

}
