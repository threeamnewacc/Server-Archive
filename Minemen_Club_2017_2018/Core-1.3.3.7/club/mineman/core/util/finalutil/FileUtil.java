// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.util.finalutil;

import java.io.InputStream;
import java.security.MessageDigest;
import java.io.FileInputStream;

public final class FileUtil
{
    public static byte[] createChecksum(final String filename) throws Exception {
        final InputStream fis = new FileInputStream(filename);
        final byte[] buffer = new byte[1024];
        final MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;
        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
        fis.close();
        return complete.digest();
    }
    
    public static String getMD5Checksum(final String filename) throws Exception {
        final byte[] b = createChecksum(filename);
        final StringBuilder result = new StringBuilder();
        for (final byte aB : b) {
            result.append(Integer.toString((aB & 0xFF) + 256, 16).substring(1));
        }
        return result.toString();
    }
}
