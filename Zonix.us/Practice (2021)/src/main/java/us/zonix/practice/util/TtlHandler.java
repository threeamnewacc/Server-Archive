package us.zonix.practice.util;

public interface TtlHandler<E> {
    void onExpire(E var1);

    long getTimestamp(E var1);
}
