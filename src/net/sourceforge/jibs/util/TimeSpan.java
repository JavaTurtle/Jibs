package net.sourceforge.jibs.util;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * DEPRACTED - will use commons
 *
 */
public class TimeSpan {
    static final int NULL = (int) Math.pow(2.0, 31.0);

    // private double start = NULL;
    // private double end = -NULL;
    private long start = NULL;
    private long end = -NULL;

    /**
     *
     */
    public TimeSpan() {
    }

    public TimeSpan(long t0, long tn) {
        set(t0, tn);
    }

    public TimeSpan(TimeSpan ts) {
        set(ts.start, ts.end);
    }

    public void setStart(long t) {
        start = t;
    }

    public void setEnd(long t) {
        end = t;
    }

    public void set(long t0, long tn) {
        start = t0;
        end = tn;
    }

    public void setNull() {
        start = NULL;
        end = -NULL;
    }

    public void setEarliest(long t) {
        start = Math.min(start, t);
    }

    public void setLatest(long t) {
        end = Math.max(end, t);
    }

    public void include(long t0) {
        setEarliest(t0);
        setLatest(t0);
    }

    public void include(long t0, long tn) {
        setEarliest(t0);
        setLatest(tn);
    }

    public void include(TimeSpan ts) {
        setEarliest(ts.getStart());
        setLatest(ts.getEnd());
    }

    public boolean contains(TimeSpan ts) {
        return contains(ts.getStart(), ts.getEnd());
    }

    public boolean contains(long startTime, long endTime) {
        if ((startTime >= start) && (endTime <= end)) {
            return true;
        }

        return false;
    }

    public boolean overlaps(TimeSpan ts) {
        return overlaps(ts.getStart(), ts.getEnd());
    }

    public boolean overlaps(long startTime, long endTime) {
        if ((this.isBefore(startTime) && this.isBefore(endTime)) ||
                (this.isAfter(startTime) && this.isAfter(endTime))) {
            return false;
        }

        return true;
    }

    public boolean contains(long dt) {
        if ((dt >= start) && (dt <= end)) {
            return true;
        }

        return false;
    }

    public boolean isBefore(long dt) {
        return (end < dt);
    }

    public boolean isAfter(long dt) {
        return (start > dt);
    }

    public boolean isValid() {
        if ((start == NULL) || (end == -NULL)) {
            return false;
        }

        return true;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public long getDuration() {
        if (isValid()) {
            return (end - start);
        }

        return 0;
    }

    public long size() {
        if (isValid()) {
            return (end - start);
        }

        return 0;
    }

    public void setCenter(long centerTime) {
        setCenter(centerTime, getDuration());
    }

    public void setCenter(long centerTime, long duration) {
        long halfWidth = duration / 2;

        set(centerTime - halfWidth, centerTime + halfWidth);
    }

    public void move(long secs) {
        set(start + secs, end + secs);
    }

    public void moveStart(long newStart) {
        move(newStart - start);

        // longdur = getDuration();
        // set (newStart, newStart + dur);
    }

    public long getCenter() {
        if (isValid()) {
            return start + (getDuration() / 2);
        }

        return 0;
    }

    public boolean equals(TimeSpan ts) {
        if ((ts.getStart() == start) && (ts.getEnd() == end)) {
            return true;
        }

        return false;
    }

    public boolean nearlyEquals(TimeSpan ts) {
        if (((int) (ts.getStart() * 100) == (int) (start * 100)) &&
                ((int) (ts.getEnd() * 100) == (int) (end * 100))) {
            return true;
        }

        return false;
    }

    public boolean isNull() {
        if ((start == NULL) && (end == -NULL)) {
            return true;
        }

        return false;
    }

    public String toString() {
        return timeToString();
    }

    public String timeToString() {
        GregorianCalendar diffDate = new GregorianCalendar();
        Date time = new Date(end - start);

        diffDate.setTime(time);

        int minutes = diffDate.get(GregorianCalendar.MINUTE);
        int seconds = diffDate.get(GregorianCalendar.SECOND);
        String s = "idle " + minutes + ":" + seconds;

        return (s);
    }
} // end of class
