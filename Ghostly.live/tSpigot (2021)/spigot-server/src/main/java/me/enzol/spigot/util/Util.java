package me.enzol.spigot.util;

import java.nio.channels.*;
import java.text.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class Util
{
    private static final int[] decimalPlaces;
    public static final DecimalFormat df;

    static {
        decimalPlaces = new int[] { 0, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000 };
        df = new DecimalFormat("#.#");
    }

    public static void download(final File file, final String from) throws Exception {
        final URL url = new URL(from);
        final InputStream stream = url.openStream();
        final ReadableByteChannel channel = Channels.newChannel(stream);
        final FileOutputStream out = new FileOutputStream(file);
        out.getChannel().transferFrom(channel, 0L, Long.MAX_VALUE);
    }

    public static void Try(final TryLambda r) {
        try {
            r.run();
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static void Try(final TryLambda _try, final Catchlambda _catch) {
        try {
            _try.run();
        }
        catch (Throwable throwable) {
            _catch.run(throwable);
        }
    }

    public static void Try(final TryLambda _try, final Catchlambda _catch, final Runnable _finally) {
        try {
            _try.run();
        }
        catch (Throwable throwable) {
            _catch.run(throwable);
            return;
        }
        finally {
            _finally.run();
        }
        _finally.run();
    }

    public static void sleep(final long time) {
        try {
            Thread.sleep(time);
        }
        catch (Throwable t) {}
    }

    public static void sleep(final long time, final int nano) {
        try {
            Thread.sleep(time, nano);
        }
        catch (Throwable t) {}
    }

    public static void endless(final Runnable task, final long sleep) {
        endless(task, sleep, false);
    }

    public static void endless(final Runnable task, final long sleep, final boolean front) {
        try {
            while (true) {
                if (front) {
                    sleep(sleep);
                }
                Try(task::run, Throwable::printStackTrace);
                if (front) {
                    continue;
                }
                sleep(sleep);
            }
        }
        catch (Throwable t) {}
    }

    public static boolean IfError(final TryLambda _try) {
        try {
            _try.run();
        }
        catch (Throwable t) {
            return true;
        }
        return false;
    }

    public static void TryIf(final TryLambda r, final boolean b) {
        if (b) {
            try {
                r.run();
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public static void TryIfNull(final Object o, final TryLambda r) {
        if (o == null) {
            try {
                r.run();
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public static void TryIfNotNull(final Object o, final TryLambda r) {
        if (o != null) {
            try {
                r.run();
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public static boolean contains(final String[] strings, final String string) {
        for (final String s : strings) {
            if (s.contains(string)) {
                return true;
            }
        }
        return false;
    }

    public static <T> T[] trim(final T[] array, final int amount) {
        return Arrays.copyOf(array, array.length - amount);
    }

    public static String compile(final Object[] objs) {
        return compile(objs, " ");
    }

    public static String compile(final Object[] objs, final String seperator) {
        final StringBuilder builder = new StringBuilder();
        for (final Object s : objs) {
            if (builder.length() != 0) {
                builder.append(seperator);
            }
            builder.append(s.toString());
        }
        return builder.toString();
    }

    public static String compile(final int[] ints, final String seperator) {
        final StringBuilder builder = new StringBuilder();
        for (final Integer s : ints) {
            if (builder.length() != 0) {
                builder.append(seperator);
            }
            builder.append(s.toString());
        }
        return builder.toString();
    }

    public static String compile(final Object[] objs, final String seperator, final int start) {
        final StringBuilder builder = new StringBuilder();
        for (int i = start; i < objs.length; ++i) {
            final Object s = objs[i];
            if (builder.length() != 0) {
                builder.append(seperator);
            }
            builder.append(s.toString());
        }
        return builder.toString();
    }

    public static String getTime() {
        return getTime(-1L);
    }

    public static String getTime(final long date) {
        final Date d = (date == -1L) ? new Date() : new Date(date);
        final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return f.format(d);
    }

    public static UUID toUUID(final String uuid) {
        if (uuid.contains("-")) {
            return UUID.fromString(uuid);
        }
        return UUID.fromString(uuid.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5"));
    }

    public static double format(final double d, final int dec) {
        return (long)(d * Util.decimalPlaces[dec] + 0.5) / (double)Util.decimalPlaces[dec];
    }

    public static String formatToString(final double d, final int dec) {
        return Double.toString(format(d, dec));
    }

    public static void close(final Closeable... closeables) {
        try {
            for (final Closeable closeable : closeables) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close(final AutoCloseable... closeables) {
        try {
            for (final AutoCloseable closeable : closeables) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean equals(final int i, final int[] array) {
        for (final int ai : array) {
            if (i == ai) {
                return true;
            }
        }
        return false;
    }

    public static void injectURL(final URL url) {
        final URLClassLoader systemClassLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
        final Class<URLClassLoader> classLoaderClass = URLClassLoader.class;
        try {
            final Method method = classLoaderClass.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(systemClassLoader, url);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static String formatMillis(final Long milis) {
        final double seconds = Math.max(0L, milis) / 1000.0;
        final double minutes = seconds / 60.0;
        final double hours = minutes / 60.0;
        final double days = hours / 24.0;
        final double weeks = days / 7.0;
        final double months = days / 31.0;
        final double years = months / 12.0;
        if (years >= 1.0) {
            return String.valueOf(Util.df.format(years)) + " year" + ((years != 1.0) ? "s" : "");
        }
        if (months >= 1.0) {
            return String.valueOf(Util.df.format(months)) + " month" + ((months != 1.0) ? "s" : "");
        }
        if (weeks >= 1.0) {
            return String.valueOf(Util.df.format(weeks)) + " week" + ((weeks != 1.0) ? "s" : "");
        }
        if (days >= 1.0) {
            return String.valueOf(Util.df.format(days)) + " day" + ((days != 1.0) ? "s" : "");
        }
        if (hours >= 1.0) {
            return String.valueOf(Util.df.format(hours)) + " hour" + ((hours != 1.0) ? "s" : "");
        }
        if (minutes >= 1.0) {
            return String.valueOf(Util.df.format(minutes)) + " minute" + ((minutes != 1.0) ? "s" : "");
        }
        return String.valueOf(Util.df.format(seconds)) + " second" + ((seconds != 1.0) ? "s" : "");
    }

    public interface Catchlambda
    {
        void run(final Throwable p0);
    }

    public interface TryLambda
    {
        void run() throws Exception;
    }
}
