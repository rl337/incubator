package org.rl337.math.types.spreadsheet.cells;

import org.rl337.math.Spreadsheet;
import org.rl337.math.types.spreadsheet.Cell;
import org.rl337.math.types.spreadsheet.CellType;

public class Text extends Cell {

    public Text(Spreadsheet spreadsheet, CellType type, String value) {
        super(spreadsheet, CellType.Text, value);
    }

    @Override
    public String getStringValue() {
        return getRawValue();
    }

    @Override
    public double getDoubleValue() {
        return Double.NaN;
    }

}
