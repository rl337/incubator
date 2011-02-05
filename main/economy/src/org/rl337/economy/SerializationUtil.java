package org.rl337.economy;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SerializationUtil {
    private static final Logger smLogger = LoggerFactory.getLogger(SerializationUtil.class);
    
    public static <T> T load(Class<T> clazz, File f) {
        Gson gson = new Gson();
        
        try {
            FileReader reader = new FileReader(f);
            T result = gson.fromJson(reader, clazz);
            return result;
        } catch (IOException e) {
            return null;
        }
    }
    
    public static <T> T load(Type t, File f) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        
        try {
            FileReader reader = new FileReader(f);
            T result = gson.fromJson(reader, t);
            return result;
        } catch (IOException e) {
            return null;
        }
    }
    
    public static <T> boolean write(T obj, File f) {
        if (f == null) {
            return false;
        }

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String sb = gson.toJson(obj);
        
        try {
            FileWriter fw = new FileWriter(f);
            fw.write(sb.toString());
            fw.close();
        } catch (IOException e) {
            smLogger.error("Could not write to file: " + f.getPath());
            return false;
        }
        
        return true;

    }

}
