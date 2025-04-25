package us.zonix.practice.board;

import java.text.DecimalFormat;
import org.apache.commons.lang.time.DurationFormatUtils;

public class BoardTimer {
    private static final DecimalFormat SECONDS_FORMATTER = new DecimalFormat("#0.0");
    private final Board board;
    private final String id;
    private final double duration;
    private final long end;

    public BoardTimer(Board board, String id, double duration) {
        this.board = board;
        this.id = id;
        this.duration = duration;
        this.end = (long)((double)System.currentTimeMillis() + duration * 1000.0);
        if (board != null) {
            board.getTimers().add(this);
        }
    }

    public String getFormattedString(BoardTimer.TimerType format) {
        return format == BoardTimer.TimerType.SECONDS
            ? SECONDS_FORMATTER.format((double)((float)(this.end - System.currentTimeMillis()) / 1000.0F))
            : DurationFormatUtils.formatDuration(this.end - System.currentTimeMillis(), "mm:ss");
    }

    public void cancel() {
        if (this.board != null) {
            this.board.getTimers().remove(this);
        }
    }

    public Board getBoard() {
        return this.board;
    }

    public String getId() {
        return this.id;
    }

    public double getDuration() {
        return this.duration;
    }

    public long getEnd() {
        return this.end;
    }

    public static enum TimerType {
        SECONDS,
        MINUTES,
        HOURS;
    }
}
