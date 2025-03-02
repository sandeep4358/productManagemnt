package com.test.utility;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelReaderUtil {

    /**
     * Reads the first sheet of the specified Excel file and returns the data as a list of rows.
     * Each row is represented as a List of String values.
     *
     * @param filePath the path to the Excel file (.xlsx)
     * @return a List of rows with each row as a List of String cell values
     */
    public static List<List<String>> readExcelFile(String filePath) {
        List<List<String>> excelData = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            // Get the first sheet from the workbook
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Iterate over each row in the sheet
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                List<String> rowData = new ArrayList<>();

                // Iterate over each cell in the row
                for (Cell cell : row) {
                    String cellValue;
                    switch (cell.getCellType()) {
                        case STRING:
                            cellValue = cell.getStringCellValue();
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                cellValue = cell.getDateCellValue().toString();
                            } else {
                                cellValue = Double.toString(cell.getNumericCellValue());
                            }
                            break;
                        case BOOLEAN:
                            cellValue = Boolean.toString(cell.getBooleanCellValue());
                            break;
                        case FORMULA:
                            cellValue = cell.getCellFormula();
                            break;
                        case BLANK:
                            cellValue = "";
                            break;
                        default:
                            cellValue = "";
                    }
                    rowData.add(cellValue);
                }
                excelData.add(rowData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return excelData;
    }

}
