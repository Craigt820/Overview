package sample;

import javafx.scene.control.ListCell;

public final class CellFactories {

    public static class ExcelHeaderCell extends ListCell<ExcelHandler.ExcelHeader> {
        public ExcelHeaderCell() {
            super();
        }

        @Override
        protected void updateItem(ExcelHandler.ExcelHeader item, boolean empty) {
            if (item != null) {
                setText(item.getData());
            }
            super.updateItem(item, empty);
        }
    }

}
