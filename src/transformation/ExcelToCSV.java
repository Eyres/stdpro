package transformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelToCSV {

    public static String removeAccents(String text) { 
        return Normalizer.normalize(text, Normalizer.Form.NFD) .replaceAll("\\p{IsM}+", "");
    }

    public static int convertToXlsx(File inputFile, File outputFile) throws IOException {
        // For storing data into CSV files
        StringBuffer cellValue = new StringBuffer();
        int cpt = 0;
        FileOutputStream fos = new FileOutputStream(outputFile);

        // Get the workbook instance for XLSX file
        @SuppressWarnings("resource")
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(inputFile));

        // Get first sheet from the workbook
        XSSFSheet sheet = wb.getSheetAt(0);

        // Iterate through each rows from first sheet
        Iterator<Row> rowIterator = sheet.iterator();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            // For each row, iterate through each columns
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                switch (cell.getCellType()) {

                case Cell.CELL_TYPE_BOOLEAN:
                    cellValue.append(cell.getBooleanCellValue() + ";");
                    break;

                case Cell.CELL_TYPE_NUMERIC:
                    cellValue.append(cell.getNumericCellValue() + ";");
                    break;

                case Cell.CELL_TYPE_STRING:
                    cellValue.append(cell.getStringCellValue() + ";");
                    break;

                case Cell.CELL_TYPE_BLANK:
                    cellValue.append("" + ";");
                    break;

                default:
                    cellValue.append(cell + ";");
                }
            }
            cpt++;
            cellValue.append(System.getProperty("line.separator"));
        }

        fos.write(cellValue.toString().getBytes());
        fos.close();
        return cpt;
    }

    public static int convertToXls(File inputFile, File outputFile) throws IOException {
        // For storing data into CSV files
        StringBuffer cellDData = new StringBuffer();
        int cpt = 0;
        FileOutputStream fos = new FileOutputStream(outputFile);

        // Get the workbook instance for XLS file
        @SuppressWarnings("resource")
        HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(inputFile));
        // Get first sheet from the workbook
        HSSFSheet sheet = workbook.getSheetAt(0);

        // Iterate through each rows from first sheet
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            // For each row, iterate through each columns
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                switch (cell.getCellType()) {

                case Cell.CELL_TYPE_BOOLEAN:
                    cellDData.append(cell.getBooleanCellValue() + ";");
                    break;

                case Cell.CELL_TYPE_NUMERIC:
                    cellDData.append(cell.getNumericCellValue() + ";");
                    break;

                case Cell.CELL_TYPE_STRING:
                    cellDData.append(cell.getStringCellValue() + ";");
                    break;

                case Cell.CELL_TYPE_BLANK:
                    cellDData.append("" + ";");
                    break;

                default:
                    cellDData.append(cell + ";");
                }
            }	
            cpt++;
            cellDData.append(System.getProperty("line.separator"));
        }

        fos.write(cellDData.toString().getBytes());
        fos.close();
        return cpt;
    }
}