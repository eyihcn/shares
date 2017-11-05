package eyihcn.shares;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class ExcelUtils {

	public static String getCellValue(Row row, int i) {

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

	public static Workbook newWorkbook(FileInputStream fileInputStream, String absPath) throws IOException {

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

}
