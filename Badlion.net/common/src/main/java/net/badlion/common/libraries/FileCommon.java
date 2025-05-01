package net.badlion.common;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FileCommon {

    public static void gzip(String inFile) {
        byte[] buffer = new byte[1024];
        GZIPOutputStream gzos = null;
        FileInputStream in = null;

        try {
            gzos = new GZIPOutputStream(new FileOutputStream(inFile + ".gz"));
            in = new FileInputStream(inFile);

            int len;
            while ((len = in.read(buffer)) > 0) {
                gzos.write(buffer, 0, len);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (gzos != null) {
                    gzos.finish();
                    gzos.close();
                }

                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String gunzipToString(String inFile) {
        char[] buffer = new char[1024];

        GZIPInputStream gzis = null;
        StringWriter writer = new StringWriter();
        Reader reader = null;

        try {
            gzis = new GZIPInputStream(new FileInputStream(inFile));
            reader = new InputStreamReader(gzis, "UTF-8");

            int len;
            while ((len = reader.read(buffer)) > 0) {
                writer.write(buffer, 0, len);
            }

        } catch(IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (gzis != null) {
                    gzis.close();
                }

                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return writer.toString();
    }

}
