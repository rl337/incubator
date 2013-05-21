package org.rl337.math.types.spreadsheet;

import org.rl337.math.Spreadsheet;


public abstract class Cell {
    public static final String ERROR_STRING = "ERR";
    
    private CellType mType;
    private String mValue;
    private Spreadsheet mSpreadsheet;
    
    public Cell(Spreadsheet spreadsheet, CellType type, String value) {
        mType = type;
        mValue = value;
        mSpreadsheet = spreadsheet;
    }
    
    protected Spreadsheet getSpreadsheet() {
        return mSpreadsheet;
    }
    
    protected void setType(CellType type) {
        mType = type;
    }
    
    public void setRawValue(String value) {
        mValue = value;
    }
    
    public String getRawValue() {
        return mValue;
    }
    
    public CellType getType() {
        return mType;
    }
    
    public abstract String getStringValue();
    public abstract double getDoubleValue();
}
