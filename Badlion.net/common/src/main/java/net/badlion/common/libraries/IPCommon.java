package net.badlion.common.libraries;

public class IPCommon {

    public static long toLongIP(byte[] bytes) {
        long val = 0;
        for (int i = 0; i < bytes.length; i++) {
            val <<= 8;
            val |= bytes[i] & 0xff;
        }
        return val;
    }

    public static byte[] ipToBytes(long bytes) {
        return new byte[] {
                (byte) ((bytes >>> 24) & 0xff),
                (byte) ((bytes >>> 16) & 0xff),
                (byte) ((bytes >>> 8) & 0xff),
                (byte) ((bytes) & 0xff),
        };
    }
}
