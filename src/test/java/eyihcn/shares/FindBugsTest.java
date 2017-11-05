package eyihcn.shares;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eyihcn.dao.DayLineFromSouHuRepository;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {  "classpath:/spring-data-mongdb.xml" })
public class FindBugsTest {

	
	@Autowired
	@Qualifier("dayLineFromSouHuDao")
	DayLineFromSouHuRepository dayLineFromSouHuDao;
	
	@Test
	public void testExportExecel() {
		String absPath = "C:\\Users\\lenovo\\Desktop\\000728.xls";
		FileInputStream in = null;
		try {
			in = new FileInputStream(absPath);
			Workbook wb = ExcelUtils.newWorkbook(in, absPath);
			Sheet sheet = wb.getSheetAt(0);
			Iterator<Row> iterRow = sheet.iterator();
			iterRow.next();
			for (; iterRow.hasNext();) {
				Row next = iterRow.next();
				// 日期
				String cellVal = ExcelUtils.getCellValue(next, 0);
				String date = cellVal.replaceAll("，", ",").split(",")[0];
				// 中文名称
				if (!dayLineFromSouHuDao.checkExistsByDateAndSharesCode(date, "000728")){
					System.out.println(date);
				}
			}
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
	
}
