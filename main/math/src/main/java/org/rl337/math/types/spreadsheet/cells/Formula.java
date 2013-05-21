package org.rl337.math.types.spreadsheet.cells;

import org.rl337.math.Spreadsheet;
import org.rl337.math.types.spreadsheet.Cell;
import org.rl337.math.types.spreadsheet.CellType;

public class Formula extends Cell {

    public Formula(Spreadsheet spreadsheet, CellType type, String value) {
        super(spreadsheet, type, value);
    }

    @Override
    public String getStringValue() {
        return null;
    }

    @Override
    public double getDoubleValue() {
        return 0;
    }

}
