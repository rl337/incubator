package org.rl337.economy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SerializationUtil {
    private static final Logger smLogger = LoggerFactory.getLogger(SerializationUtil.class);
    
    public static <T> T loadJSON(Class<T> clazz, File f) {
        Gson gson = new Gson();
        
        try {
            FileReader reader = new FileReader(f);
            T result = gson.fromJson(reader, clazz);
            reader.close();
            return result;
        } catch (IOException e) {
            smLogger.error("Error while reading file: " + f, e);
            return null;
        }
    }
    
    public static <T> T loadJSON(Type t, File f) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        
        try {
            FileReader reader = new FileReader(f);
            T result = gson.fromJson(reader, t);
            reader.close();
            return result;
        } catch (IOException e) {
            smLogger.error("Error while reading file: " + f, e);
            return null;
        }
    }
    
    public static <T> boolean writeJSON(T obj, File f) {
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
    
    public static boolean writeBinary(Object o, File f) {
        try {
            ObjectOutput out = new ObjectOutputStream(new FileOutputStream(f));
            out.writeObject(o);
            out.close();
            return true;
        } catch (IOException e) {
            smLogger.error("Could not serialize object to file: " + f.getPath());
        }
        
        return false;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T loadBinary(File f) {
        try {
            ObjectInput in = new ObjectInputStream(new FileInputStream(f));
            Object o = in.readObject();
            in.close();
            
            return (T) o;
        } catch (IOException e) {
            smLogger.error("Error while deserializing type.", e);
        } catch (ClassNotFoundException e) {
            smLogger.error("Couldn't find class to deserialize into.", e);
        }
        
        return null;
    }

}
