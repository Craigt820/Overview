package sample;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;

public class ExcelUtils {

    public static String evaluateCell(Workbook wb, Cell cell) {
        String eval = null;
        final FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

        switch (evaluator.evaluateInCell(cell).getCellType()) {
            case BOOLEAN:
                eval = String.valueOf(cell.getBooleanCellValue());
                break;
            case NUMERIC:
                String cellValueLeadingZeroCheck = NumberToTextConverter.toText(cell.getNumericCellValue());
                eval = cellValueLeadingZeroCheck;
                break;
            case STRING:
                eval = cell.getStringCellValue();
                break;
            case BLANK:
                eval = "";
                break;
            case ERROR:
                System.out.println(cell.getErrorCellValue());
                break;
            case FORMULA:
                eval = evaluator.evaluateInCell(cell).getStringCellValue();
                break;
        }
        return eval;
    }
}
