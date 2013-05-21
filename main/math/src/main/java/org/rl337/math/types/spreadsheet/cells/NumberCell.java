package org.rl337.math.types.spreadsheet.cells;

import java.text.DecimalFormat;
import java.text.ParseException;

import org.rl337.math.Spreadsheet;
import org.rl337.math.types.spreadsheet.Cell;
import org.rl337.math.types.spreadsheet.CellType;

public class NumberCell extends Cell {
    private DecimalFormat mFormatter;
    private DecimalFormat mParserFormatter;

    public NumberCell(Spreadsheet spreadsheet, String format, String parseFormat, String value) {
        super(spreadsheet, CellType.Number, value);
        
        if (format != null) {
            mFormatter = new DecimalFormat(format);
        }
        
        if (parseFormat != null) {
            mParserFormatter = new DecimalFormat(parseFormat);
        }
    }
    
    public NumberCell(Spreadsheet spreadsheet, String format, String value) {
        this(spreadsheet, format, null, value);
    }
    
    public NumberCell(Spreadsheet spreadsheet, String value) {
        this(spreadsheet, null, null, value);
    }

    @Override
    public String getStringValue() {
        
        String raw = getRawValue();
        if (raw == null) {
            return null;
        }
        
        double d = getDoubleValue();
        if (d == Double.NaN) {
            return ERROR_STRING;
        }
        
        if (mFormatter == null) {
            return Double.toString(d);
        }
        
        try {
            return mFormatter.format(getDoubleValue());
        } catch (Exception e) {
            return ERROR_STRING;
        }
    }

    @Override
    public double getDoubleValue() {
        
        String raw = getRawValue();
        if (raw == null) {
            return Double.NaN;
        }
        
        if (mParserFormatter == null) {
            try {
                return Double.parseDouble(raw);
            } catch (NumberFormatException e) {
                return Double.NaN;
            }
        }

        try {
            Number n = mParserFormatter.parse(raw);
            return n.doubleValue();
        } catch (ParseException e) {
            return Double.NaN;
        }
    }

}
