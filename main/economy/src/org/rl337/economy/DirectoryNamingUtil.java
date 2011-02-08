package org.rl337.economy;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class DirectoryNamingUtil {
    private String mPrefix;
    private File mParent;
    
    public DirectoryNamingUtil(String prefix, File parent) {
        mPrefix = prefix;
        mParent = parent;
    }
    
    public File getDateFormattedSubDirectory(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss-SSSS");
        String name = format.format(date);
        
        return new File(mParent, mPrefix + name);
    }

    public File getLatestDateFormattedSubDirectory() {
        if (mParent == null || !mParent.isDirectory()) {
            return null;
        }
        
        if (mParent == null) {
            return null;
        }
        
        final String filePrefix = mPrefix.trim();
        if (filePrefix.isEmpty()) {
            return null;
        }
        
        File[] subdirs = mParent.listFiles(
            new FileFilter() {
                @Override
                public boolean accept(File file) {
                    String fileName = file.getName();
                    if (file.isDirectory() && fileName.startsWith(filePrefix)) {
                        return true;
                    }
                    
                    return false;
                }
            }
        );
        
        if (subdirs == null || subdirs.length < 1) {
            return null;
        }

        Arrays.sort(subdirs, 
            new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return o2.getName().compareTo(o1.getName());
                }
            }
        );
        
        return subdirs[0];
    }
    
    public String getPrefix() {
        return mPrefix;
    }
    
    public File getParentDirectory() {
        return mParent;
    }
}
