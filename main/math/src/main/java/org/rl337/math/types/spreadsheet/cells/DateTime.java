package org.rl337.math.types.spreadsheet.cells;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.rl337.math.Spreadsheet;
import org.rl337.math.types.spreadsheet.Cell;
import org.rl337.math.types.spreadsheet.CellType;

public class DateTime extends Cell {
    private SimpleDateFormat mParserFormat;
    private SimpleDateFormat mFormat;
    

    public DateTime(Spreadsheet spreadsheet, String format, String parserFormat, String value) {
        super(spreadsheet, CellType.DateTime, value);
        
        mFormat = new SimpleDateFormat(format);
        mParserFormat = new SimpleDateFormat(parserFormat);
    }

    @Override
    public String getStringValue() {
        Date d = getDateValue();
        
        if (d == null) {
            return null;
        }
        
        return mFormat.format(d);
    }
    
    @Override
    public double getDoubleValue() {
        Date d = getDateValue();
        
        if (d == null) {
            return Double.NaN;
        }
        
        return d.getTime();
    }
    
    public Date getDateValue() {
        String raw = getRawValue();
        if (raw == null) {
            return null;
        }

        try {
            Date d = mParserFormat.parse(raw);
            if (d == null) {
                return d;
            }

        } catch (ParseException e) {
        }
        
        return null;
    }


}
