/*
 * Stream I/O util
 */
package common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author iychoi
 */
public class StreamUtil {
    public static BufferedReader getReader(File file) throws IOException {
        if(file == null || !file.exists() || !file.isFile())
            throw new IOException("Cannot find file");
        
        FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        
        return br;
    }
    
    public static String readFileString(File file) throws Exception {
        FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        String full = "";
        int read = 0;
        char[] cbuf = new char[4096];
        while ((read = br.read(cbuf)) > 0) {
            full += new String(cbuf, 0, read);
        }
        
        br.close();
        return full;
    }
}
