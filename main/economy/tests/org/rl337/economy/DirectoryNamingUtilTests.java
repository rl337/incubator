package org.rl337.economy;
import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.rl337.economy.DirectoryNamingUtil;

import junit.framework.TestCase;


public class DirectoryNamingUtilTests extends TestCase {
    private File mDirectory;
    private DirectoryNamingUtil mUtil;
    
    public void setUp() throws Exception {
        mDirectory = File.createTempFile("DirectoryNamingUtilTests", ".dir");
        mDirectory.delete();
        mDirectory.mkdir();
        
        mUtil = new DirectoryNamingUtil(Long.toHexString(System.currentTimeMillis()), mDirectory);
    }
    
    public void testGetDateFormattedSubDirectory() {
        Calendar calendar = Calendar.getInstance();
        // setting month on calendar is 0 based, so 3 is actually April.
        calendar.set(2010, 3, 24, 12, 3, 29);
        calendar.set(Calendar.MILLISECOND, 42);
        
        Date date = calendar.getTime();
        
        File f = mUtil.getDateFormattedSubDirectory(date);
        
        assertEquals("Parent directory should be same.", mDirectory, f.getParentFile());
        String expectedName = mUtil.getPrefix() + "20100424-120329-0042";
        assertEquals("Directory name should match expected", expectedName, f.getName());
        
    }
    
    public void testGetLatestDateFormattedDirectory() {
        long time = System.currentTimeMillis();
        long offset = 0;

        for(int i = 0; i < 5; i++) {
            Date date = new Date(time + offset);
            File d = mUtil.getDateFormattedSubDirectory(date);
            assertTrue("mkdir should return true", d.mkdir());
            // subtract some random amount of time.
            offset -= 3600 * 17;
        }
        
        File expected = mUtil.getDateFormattedSubDirectory(new Date(time));
        
        
        File f = mUtil.getLatestDateFormattedSubDirectory();
        assertEquals("latest should be expected dir.", expected, f);
        
    }
    
}
