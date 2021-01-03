package sample;

import javafx.scene.layout.GridPane;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class ExcelHandler {


    public class ExcelHeader extends GridPane {
        private String data;
        private int cellIndex;

        public ExcelHeader(String data, int cellIndex) {
            this.data = data;
            this.cellIndex = cellIndex;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public int getCellIndex() {
            return cellIndex;
        }

        public void setCellIndex(int cellIndex) {
            this.cellIndex = cellIndex;
        }

        @Override
        public String toString() {
            System.out.printf("Data: %s | Cell Index: %d \n", getData(), getCellIndex());
            return super.toString();
        }
    }

    private int numSheets;
    private File file;
    private Map<String, String> examples = new TreeMap<>();

    public ExcelHandler(File file) {
        this.file = file;
    }

    public ArrayList<ExcelHeader> readHeaders(File file) {
        ArrayList<ExcelHeader> headers = new ArrayList<>();

        FileInputStream fis;
        Workbook wb = null;
        try {
            fis = new FileInputStream(file);
            wb = new XSSFWorkbook(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        numSheets = wb.getNumberOfSheets();
        for (int sheetIdx = 0; sheetIdx < numSheets; sheetIdx++) {
            Sheet sheetObj = wb.getSheetAt(sheetIdx);
            if (sheetObj != null) {
                Row headerRow = sheetObj.getRow(0);
                Row exampleRow = sheetObj.getRow(1);
                if (headerRow != null) {
                    int cols = headerRow.getPhysicalNumberOfCells();
                    for (int i = 0; i < cols; i++) {
                        Cell cell = headerRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        ExcelHeader header = new ExcelHeader(ExcelUtils.evaluateCell(wb, cell), cell.getColumnIndex());
                        if (!header.getData().isEmpty()) {
                            headers.add(header);
                        }
                    }
                } else {
                    break;
                }

                if (exampleRow != null) {
                    int cols = exampleRow.getPhysicalNumberOfCells();
                    for (int i = 0; i < cols; i++) {
                        Cell cell = exampleRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        examples.put(ExcelUtils.evaluateCell(wb, headerRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)), ExcelUtils.evaluateCell(wb, cell));
                    }
                }
            } else {
                break;
            }
        }
        return headers;
    }


    public void readData(File file) {

    }

    public Map<String, String> getExamples() {
        return examples;
    }

    public void setExamples(Map<String, String> examples) {
        this.examples = examples;
    }

    public int getNumSheets() {
        return numSheets;
    }

    public void setNumSheets(int numSheets) {
        this.numSheets = numSheets;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
