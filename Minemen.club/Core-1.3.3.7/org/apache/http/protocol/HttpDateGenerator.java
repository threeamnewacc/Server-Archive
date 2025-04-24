// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.protocol;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.text.DateFormat;
import java.util.TimeZone;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.SAFE)
public class HttpDateGenerator
{
    public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final TimeZone GMT;
    private final DateFormat dateformat;
    private long dateAsLong;
    private String dateAsText;
    
    public HttpDateGenerator() {
        this.dateAsLong = 0L;
        this.dateAsText = null;
        (this.dateformat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US)).setTimeZone(HttpDateGenerator.GMT);
    }
    
    public synchronized String getCurrentDate() {
        final long now = System.currentTimeMillis();
        if (now - this.dateAsLong > 1000L) {
            this.dateAsText = this.dateformat.format(new Date(now));
            this.dateAsLong = now;
        }
        return this.dateAsText;
    }
    
    static {
        GMT = TimeZone.getTimeZone("GMT");
    }
}
