package com.halfroom.distribution.core.util;

import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * Excel util, create excel sheet, cell and style.
 * 
 * @param <T>
 *            generic class.
 */
@SuppressWarnings("all")
public class ExcelUtil<T> {
	private static final long MAXDATASHEET = 60000;
	private static final int COLUMNWIDTH = 4000;
	private static final String BLANK = "MINGYU";

	public HSSFCellStyle getCellStyle(HSSFWorkbook workbook, boolean isHeader) {
		HSSFCellStyle style = workbook.createCellStyle();
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setLocked(true);
		style.setWrapText(true);
		if (isHeader) {
			style.setFillForegroundColor(HSSFColor.YELLOW.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			HSSFFont font = workbook.createFont();
			font.setColor(HSSFColor.BLACK.index);
			font.setFontHeightInPoints((short) 12);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style.setFont(font);
		}
		return style;
	}

	public void generateHeader(HSSFWorkbook workbook, HSSFSheet sheet, String[] headerColumns) {
		HSSFCellStyle style = getCellStyle(workbook, true);
		Row row = sheet.createRow(0);
		row.setHeightInPoints(30);
		for (int i = 0; i < headerColumns.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(headerColumns[i]);
			sheet.autoSizeColumn((short)i); //自动调节宽度
		}
	}
	
	/**
	 * 自定义Excel列宽，每一列列宽相同
	 */
	public void generateHeaderSelfDefineColumnWidth(HSSFWorkbook workbook, HSSFSheet sheet, String[] headerColumns,int width) {
		HSSFCellStyle style = getCellStyle(workbook, true);
		Row row = sheet.createRow(0);
		row.setHeightInPoints(30);
		for (int i = 0; i < headerColumns.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(headerColumns[i]);
			sheet.setColumnWidth(i, width); //自定义宽度
		}
	}
	
	/**
	 * 自定义Excel列宽，每一列列宽可自定义
	 * 输入与列长度一致的数组
	 */
	public void generateHeaderSelfDefineColumnWidth(HSSFWorkbook workbook, HSSFSheet sheet, String[] headerColumns,int[] width) {
		HSSFCellStyle style = getCellStyle(workbook, true);
		Row row = sheet.createRow(0);
		row.setHeightInPoints(30);
		int headerLength = 0;
		int widthLength = 0;
		if(null!=headerColumns&&null!=width){
			//如果列宽数组长度与表头列数不一致，则将宽度数组填充
			if((widthLength = width.length)<(headerLength = headerColumns.length)){
				width = Arrays.copyOf(width, headerLength);
				Arrays.fill(width, widthLength==0?COLUMNWIDTH:width[0]);
			}
			for (int i = 0; i < headerLength; i++) {
				Cell cell = row.createCell(i);
				cell.setCellStyle(style);
				cell.setCellValue(headerColumns[i]);
				sheet.setColumnWidth(i, width[i]); //自定义宽度
			}
		}
	}
	
	/**
	 * 
	 * @param workbook
	 * @param sheetName
	 * @param dataset
	 * @param headerColumns
	 * @param fieldColumns
	 * @param isConvert 是否需要 转化 get 方法 之后的第一个字母 大写 true 转化 false 不转化
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public HSSFSheet creatAuditSheet(HSSFWorkbook workbook, String sheetName, List<T> dataset, String[] headerColumns, String[] fieldColumns,boolean isConvert) throws Exception {
		long sheetIndex = 1;
		long dataIndex = 1;
		int rowNum = 0;
		HSSFSheet sheet = workbook.createSheet(sheetName + sheetIndex);
		//generateHeader(workbook, sheet, headerColumns);
		HSSFCellStyle style = getCellStyle(workbook, false);
		for (T t : dataset) {
			if (dataIndex % MAXDATASHEET == 0) {
				rowNum = 0;
				sheetIndex = dataIndex / MAXDATASHEET + 1;
				sheet = workbook.createSheet(sheetName + sheetIndex);
				generateHeader(workbook, sheet, headerColumns);
				style = getCellStyle(workbook, false);
			}
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dataIndex++;
			rowNum++;
			Row row = sheet.createRow(rowNum);
			row.setHeightInPoints(25);
			for (int i = 0; i < fieldColumns.length; i++) {
				String fieldName = fieldColumns[i];
				String cellValue = "";
				String getMethodName = "";
				try {
					if(!BLANK.equals(fieldName)){
						
								
						if(isConvert){
							getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
						}else{
							getMethodName = "get" + fieldName.substring(0, 1) + fieldName.substring(1);
						}
						Class clazz = t.getClass();
						Method getMethod;
						getMethod = clazz.getMethod(getMethodName, new Class[] {});
						Object value = getMethod.invoke(t, new Object[] {});
						if (value instanceof Date) {
							Date date = (Date) value;
							cellValue = sd.format(date);
						} else {
							cellValue = null != value ? value.toString() : "";
						}
					}
					Cell cell = row.createCell(i);
					cell.setCellStyle(style);
					cell.setCellValue(cellValue);
				} catch (Exception e) {}
			}
		}
		return sheet;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HSSFSheet creatAuditSheetForPlan(HSSFWorkbook workbook, String sheetName, List<T> dataset, String[] headerColumns, String[] fieldColumns) throws Exception {
		long sheetIndex = 1;
		long dataIndex = 1;
		int rowNum = 0;
		HSSFSheet sheet = workbook.getSheet("sheet1");
		rowNum = sheet.getLastRowNum() + 2;
		if(sheet != null){
			HSSFCellStyle headStyle = getCellStyle(workbook, true);
			Row headRow = sheet.createRow(rowNum);
			headRow.setHeightInPoints(30);
			for (int i = 0; i < headerColumns.length; i++) {
				Cell cell = headRow.createCell(i);
				cell.setCellStyle(headStyle);
				cell.setCellValue(headerColumns[i]);
				sheet.autoSizeColumn((short)i); //自动调节宽度
			}
			HSSFCellStyle style = getCellStyle(workbook, false);
			for (T t : dataset) {
				if (dataIndex % MAXDATASHEET == 0) {
					rowNum = 0;
					sheetIndex = dataIndex / MAXDATASHEET + 1;
					sheet = workbook.createSheet(sheetName + sheetIndex);
					generateHeader(workbook, sheet, headerColumns);
					style = getCellStyle(workbook, false);
				}
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dataIndex++;
				rowNum++;
				Row row = sheet.createRow(rowNum);
				row.setHeightInPoints(25);
				for (int i = 0; i < fieldColumns.length; i++) {
					String fieldName = fieldColumns[i];
					String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					try {
						Class clazz = t.getClass();
						Method getMethod;
						getMethod = clazz.getMethod(getMethodName, new Class[] {});
						Object value = getMethod.invoke(t, new Object[] {});
						String cellValue = "";
						if (value instanceof Date) {
							Date date = (Date) value;
							cellValue = sd.format(date);
						} else {
							cellValue = null != value ? value.toString() : "";
						}
						Cell cell = row.createCell(i);
						cell.setCellStyle(style);
						cell.setCellValue(cellValue);
					} catch (Exception e) {}
				}
			}
		}
		return sheet;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HSSFSheet creatAuditSheetForQuestion(HSSFWorkbook workbook, String sheetName, List<T> dataset, String[] headerColumns, String[] fieldColumns) throws Exception {
		long sheetIndex = 1;
		long dataIndex = 1;
		int rowNum = 0;
		HSSFSheet sheet = workbook.getSheet("sheet1");
		rowNum = sheet.getLastRowNum() + 2;
		if(sheet != null){
			HSSFCellStyle headStyle = getCellStyle(workbook, true);
			Row headRow = sheet.createRow(rowNum);
			headRow.setHeightInPoints(30);
			for (int i = 0; i < headerColumns.length; i++) {
				Cell cell = headRow.createCell(i);
				cell.setCellStyle(headStyle);
				cell.setCellValue(headerColumns[i]);
				 sheet.autoSizeColumn((short)i); //自动调节宽度
			}
			HSSFCellStyle style = getCellStyle(workbook, false);
			for (T t : dataset) {
				if (dataIndex % MAXDATASHEET == 0) {
					rowNum = 0;
					sheetIndex = dataIndex / MAXDATASHEET + 1;
					sheet = workbook.createSheet(sheetName + sheetIndex);
					generateHeader(workbook, sheet, headerColumns);
					style = getCellStyle(workbook, false);
				}
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dataIndex++;
				rowNum++;
				Row row = sheet.createRow(rowNum);
				row.setHeightInPoints(25);
				for (int i = 0; i < fieldColumns.length; i++) {
					String fieldName = fieldColumns[i];
					String getMethodName = "";
					if(StringUtils.isNotBlank(fieldName)){
						getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					}
					try {
						Class clazz = t.getClass();
						Method getMethod;
						getMethod = clazz.getMethod(getMethodName, new Class[] {});
						Object value = getMethod.invoke(t, new Object[] {});
						String cellValue = "";
						if (value instanceof Date) {
							Date date = (Date) value;
							cellValue = sd.format(date);
						} else {
							cellValue = null != value ? value.toString() : "";
						}
						Cell cell = row.createCell(i);
						cell.setCellStyle(style);
						cell.setCellValue(cellValue);
					} catch (Exception e) {}
				}
			}
		}
		return sheet;
	}

	public void generateHeaders(HSSFWorkbook workbook, HSSFSheet sheet, String[] headerColumns) {
		HSSFCellStyle style = getCellStyle(workbook, true);
		Row row = sheet.createRow(0);
		row.setHeightInPoints(30);
		for (int i = 0; i < headerColumns.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(headerColumns[i]);
			/*sheet.setColumnWidth((short)i,4000); //自动调节宽度*/
			sheet.autoSizeColumn((short)i,true);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public HSSFSheet creatAuditSheets(HSSFWorkbook workbook, String sheetName, List<T> dataset, String[] headerColumns, String[] fieldColumns) throws Exception {
		long sheetIndex = 1;
		long dataIndex = 1;
		int rowNum = 0;
		HSSFSheet sheet = workbook.createSheet(sheetName + sheetIndex);
		generateHeaders(workbook, sheet, headerColumns);
		HSSFCellStyle style = getCellStyle(workbook, false);
		for (T t : dataset) {
			if (dataIndex % MAXDATASHEET == 0) {
				rowNum = 0;
				sheetIndex = dataIndex / MAXDATASHEET + 1;
				sheet = workbook.createSheet(sheetName + sheetIndex);
				generateHeaders(workbook, sheet, headerColumns);
				style = getCellStyle(workbook, false);
			}
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dataIndex++;
			rowNum++;
			Row row = sheet.createRow(rowNum);
			row.setHeightInPoints(25);
			for (int i = 0; i < fieldColumns.length; i++) {
				String fieldName = fieldColumns[i];
				String cellValue = "";
				String getMethodName = "";
				try {
					if(!BLANK.equals(fieldName)){
						if(!Character.isUpperCase(fieldName.substring(0, 1).toCharArray()[0])){
							getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
						}else{
							getMethodName = "get" + fieldName.substring(0, 1) + fieldName.substring(1);
						}
						
						Class clazz = t.getClass();
						Method getMethod;
						getMethod = clazz.getMethod(getMethodName, new Class[] {});
						Object value = getMethod.invoke(t, new Object[] {});
						if (value instanceof Date) {
							Date date = (Date) value;
							cellValue = sd.format(date);
						} else {
							cellValue = null != value ? value.toString() : "";
						}
					}
					Cell cell = row.createCell(i);
					cell.setCellStyle(style);
					cell.setCellValue(cellValue);
				} catch (Exception e) {}
			}
		}
		sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,0,headerColumns.length-1));
		return sheet;
	}
	public static void doResponse(HttpServletResponse response,ServletOutputStream outputStream,String fileName,HSSFWorkbook hssfWorkbook){
		try {
			fileName = URLEncoder.encode(fileName, "UTF-8")+ ".xls";
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		try {
			hssfWorkbook.write(outputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
