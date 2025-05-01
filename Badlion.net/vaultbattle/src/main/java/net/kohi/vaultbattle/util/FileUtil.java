package net.kohi.vaultbattle.util;

import java.io.*;

public class FileUtil {

    public static void deleteRecursive(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteRecursive(child);
            }
        }
        file.delete();
    }

    public static void copyRecursive(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            dest.mkdir();
            for (String file : src.list()) {
                copyRecursive(new File(src, file), new File(dest, file));
            }
        } else {
            try (InputStream in = new FileInputStream(src); OutputStream out = new FileOutputStream(dest)) {
                byte[] buf = new byte[1024];
                int len = 0;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }
}
