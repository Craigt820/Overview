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
        private String sheet;
        private String name;
        private int cellIndex;
        private int row;
        private ArrayList<Object> data = new ArrayList();

        public ExcelHeader(String sheet, String name, int row, int cellIndex) {
            this.sheet = sheet;
            this.name = name;
            this.row = row;
            this.cellIndex = cellIndex;
        }

        public String getName() {
            return name;
        }

        public String getSheet() {
            return sheet;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCellIndex() {
            return cellIndex;
        }

        public void setSheet(String sheet) {
            this.sheet = sheet;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public void setCellIndex(int cellIndex) {
            this.cellIndex = cellIndex;
        }

        public ArrayList<Object> getData() {
            return data;
        }

        public void setData(ArrayList<Object> data) {
            this.data = data;
        }

        @Override
        public String toString() {
            System.out.printf("Data: %s | Cell Index: %d \n", getName(), getCellIndex());
            return super.toString();
        }
    }

    private int numSheets;
    private File file;
    private Map<String, String> examples = new TreeMap<>();

    public ExcelHandler(File file) {
        this.file = file;
    }

    //TODO: Only reads one sheet right now! Add multiple sheet mode...
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
        numSheets = 1;
        for (int sheetIdx = 0; sheetIdx < 1; sheetIdx++) {
            assert wb != null;
            Sheet sheetObj = wb.getSheetAt(sheetIdx);
            if (sheetObj != null) {
                Row headerRow = sheetObj.getRow(0);
                Row exampleRow = sheetObj.getRow(1);
                if (headerRow != null) {
                    int cols = headerRow.getPhysicalNumberOfCells();
                    for (int i = 0; i < cols; i++) {
                        Cell cell = headerRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        ExcelHeader header = new ExcelHeader(sheetObj.getSheetName(), ExcelUtils.evaluateCell(wb, cell), cell.getRow().getRowNum(), cell.getColumnIndex());
                        for (int j = 1; j < sheetObj.getPhysicalNumberOfRows(); j++) {
                            if (sheetObj.getRow(j) != null) {
                                Object data = sheetObj.getRow(j).getCell(header.getCellIndex());
                                header.getData().add(data);
                                int finalJ = j;
//                            System.out.printf("Sheet: %s Row: %d Cell: %d Data: %s \n", sheetObj.getSheetName(), finalJ, header.getCellIndex(), data.toString());
                            }
                        }
                        if (header.getName() != null && !header.getName().isEmpty()) {
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
