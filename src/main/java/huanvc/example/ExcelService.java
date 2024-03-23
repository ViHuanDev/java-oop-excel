package huanvc.example;


import java.io.*;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelService {
    public static final int COLUMN_INDEX_ID = 0;
    public static final int COLUMN_INDEX_START_TYPE = 1;
    public static final int COLUMN_INDEX_COUNT = 2;
    public static final int COLUMN_INDEX_PROPOTION = 3;
    private static CellStyle cellStyleFormatNumber = null;

    public String currentPath = new java.io.File(".").getCanonicalPath() + "/src";

    public ExcelService() throws IOException {
    }


    public static List<ExcelModel> openFile(String path) throws IOException {
        System.out.println("File Reading." + path);
        FileInputStream file = new FileInputStream(path);

        Workbook workbook = getWorkbook(path, file);
        Sheet sheet = workbook.getSheetAt(0);

        List<ExcelModel> list = new ArrayList<>();
        // Iterate through each rows one by one
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            //For each row, iterate through all the columns
            if (row.getRowNum() < 2) {
                continue;
            }
            ExcelModel model = new ExcelModel();
            model.setId(String.valueOf((int) row.getCell(COLUMN_INDEX_ID).getNumericCellValue()));
            model.setStart_type(row.getCell(COLUMN_INDEX_START_TYPE).getStringCellValue());
            model.setCount(String.valueOf(row.getCell(COLUMN_INDEX_COUNT).getNumericCellValue()));
            model.setProportion(String.valueOf(row.getCell(COLUMN_INDEX_PROPOTION).getNumericCellValue()));
            model.setRow_index(row.getRowNum());
            list.add(model);
        }
        System.out.println("File finished reading.");
        workbook.close();
        file.close();
        return list;
    }

    private static Workbook getWorkbook(String path, FileInputStream file) {
        Workbook workbook = null;
        try {
            if (path.endsWith("xlsx")) {
                workbook = new XSSFWorkbook(file);
            } else if (path.endsWith("xls")) {
                workbook = new HSSFWorkbook(file);
            } else {
                throw new IllegalArgumentException("The specified file is not Excel file");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workbook;
    }

    public void updateRow(ExcelModel model, String path) throws IOException {
        FileInputStream file = new FileInputStream(path);
        Workbook workbook = getWorkbook(path, file);
        Sheet sheet = workbook.getSheetAt(0);

        Row row = sheet.getRow(model.getRow_index());
        row.getCell(COLUMN_INDEX_ID).setCellValue(Integer.parseInt(model.getId()));
        row.getCell(COLUMN_INDEX_START_TYPE).setCellValue(model.getStart_type());
        row.getCell(COLUMN_INDEX_COUNT).setCellValue(Integer.parseInt(model.getCount()));
        row.getCell(COLUMN_INDEX_PROPOTION).setCellValue(Double.parseDouble(model.getProportion()));
        file.close();

        FileOutputStream outPut = new FileOutputStream(path);
        workbook.write(outPut);
        outPut.close();
        workbook.close();
    }

    public void deleteRow(Integer indexRow, String path) throws IOException {
        FileInputStream file = new FileInputStream(path);
        Workbook workbook = getWorkbook(path, file);
        Sheet sheet = workbook.getSheetAt(0);

        int lastRowNum = sheet.getLastRowNum();
        if (indexRow >= 0 && indexRow < lastRowNum) {
            sheet.shiftRows(indexRow + 1, lastRowNum, -1);
        }
        if (indexRow == lastRowNum) {
            HSSFRow removingRow = (HSSFRow) sheet.getRow(indexRow);
            if (removingRow != null) {
                sheet.removeRow(removingRow);
            }
        }

        FileOutputStream outPut = new FileOutputStream(path);
        workbook.write(outPut);
        outPut.close();
        workbook.close();
    }

    public void addRow(ExcelModel model, String path) throws IOException {
        FileInputStream file = new FileInputStream(path);
        Workbook workbook = getWorkbook(path, file);
        Sheet sheet = workbook.getSheetAt(0);

        int lastRowNum = sheet.getLastRowNum();
        Row row = sheet.createRow(++lastRowNum);
        row.createCell(COLUMN_INDEX_ID).setCellValue(Integer.parseInt(model.getId()));
        row.createCell(COLUMN_INDEX_START_TYPE).setCellValue(model.getStart_type());
        row.createCell(COLUMN_INDEX_COUNT).setCellValue(Integer.parseInt(model.getCount()));
        row.createCell(COLUMN_INDEX_PROPOTION).setCellValue(Double.parseDouble(model.getProportion()));

        FileOutputStream outPut = new FileOutputStream(path);
        workbook.write(outPut);
        outPut.close();
        workbook.close();
    }

    public void exportDataFormStorage(ArrayList<ExcelModel> listData) throws IOException {
        // Create Workbook
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        // Create sheet and sheet name
        SXSSFSheet sheet = workbook.createSheet("data");
        // register the columns you wish to track and compute the column width
        sheet.trackAllColumnsForAutoSizing();
        writeHeader(sheet, 0);
        for (int i = 0; i < listData.size(); i++) {
            ExcelModel model = listData.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(COLUMN_INDEX_ID).setCellValue(Integer.parseInt(model.getId()));
            row.createCell(COLUMN_INDEX_START_TYPE).setCellValue(model.getStart_type());
            row.createCell(COLUMN_INDEX_COUNT).setCellValue(Integer.parseInt(model.getCount()));
            row.createCell(COLUMN_INDEX_PROPOTION).setCellValue(Double.parseDouble(model.getProportion()));
        }
        createOutputFile(workbook, currentPath + "/output.xlsx");
    }

    // Create output file
    private static void createOutputFile(SXSSFWorkbook workbook, String excelFilePath) throws IOException {
        File file = new File(excelFilePath);
        if (file.exists()) {
            file.delete();
            System.out.println("File deleted.");
        }

        try (OutputStream os = new FileOutputStream(excelFilePath)) {
            workbook.write(os);
        }
        workbook.close();
    }

    // Write header with format
    private static void writeHeader(SXSSFSheet sheet, int rowIndex) {
        // create CellStyle
        CellStyle cellStyle = createStyleForHeader(sheet);

        // Create row
        SXSSFRow row = sheet.createRow(rowIndex);

        // Create cells
        SXSSFCell cell = row.createCell(COLUMN_INDEX_ID);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Id");

        cell = row.createCell(COLUMN_INDEX_START_TYPE);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Start Type");

        cell = row.createCell(COLUMN_INDEX_COUNT);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Count");

        cell = row.createCell(COLUMN_INDEX_PROPOTION);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Propotion");
    }

    // Create CellStyle for header
    private static CellStyle createStyleForHeader(Sheet sheet) {
        // Create font
        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Times New Roman");
        font.setBold(true);
        font.setFontHeightInPoints((short) 14); // font size
        font.setColor(IndexedColors.WHITE.getIndex()); // text color

        // Create CellStyle
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        return cellStyle;
    }
}
