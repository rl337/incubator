package org.rl337.math.types.spreadsheet.cells;

import org.rl337.math.Spreadsheet;
import org.rl337.math.types.spreadsheet.Cell;
import org.rl337.math.types.spreadsheet.CellType;

public class General extends Cell {

    public General(Spreadsheet spreadsheet, String value) {
        super(spreadsheet, CellType.General, value);
    }
    
    public String getStringValue() {
        return getRawValue();
    }
    
    public double getDoubleValue() {
        String rawValue = getRawValue();
        if (rawValue == null) {
            return Double.NaN;
        }
        
        try {
            return Double.parseDouble(getRawValue());
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

}
