package net.badlion.gfactions.util;

public class FlatMap<V>
{

    private int FLAT_LOOKUP_SIZE;
    private int FLAT_LOOKUP_SIZE_DIVIDED_BY_2;
    private Object[] flatLookup = new Object[ FLAT_LOOKUP_SIZE * FLAT_LOOKUP_SIZE ];

    public FlatMap(int size) {
        this.FLAT_LOOKUP_SIZE = size;
        this.FLAT_LOOKUP_SIZE_DIVIDED_BY_2 = this.FLAT_LOOKUP_SIZE / 2;
        this.flatLookup = new Object[this.FLAT_LOOKUP_SIZE * this.FLAT_LOOKUP_SIZE];
    }

    public void put(int msw, int lsw, V value)
    {
        int acx = msw >= 0 ? msw + FLAT_LOOKUP_SIZE_DIVIDED_BY_2 : msw * -1;
        int acz = lsw >= 0 ? lsw + FLAT_LOOKUP_SIZE_DIVIDED_BY_2 : lsw * -1;
        if (acx < FLAT_LOOKUP_SIZE && acz < FLAT_LOOKUP_SIZE) {
            flatLookup[(acx * FLAT_LOOKUP_SIZE) + acz] = value;
        }
    }

    public void put(long key, V value)
    {
        put( LongHash.msw( key ), LongHash.lsw( key ), value );

    }

    public void remove(long key)
    {
        put( key, null );
    }

    public void remove(int msw, int lsw)
    {
        put( msw, lsw, null );
    }

    public boolean contains(int msw, int lsw)
    {
        return get( msw, lsw ) != null;
    }

    public boolean contains(long key)
    {
        return get( key ) != null;
    }

    public V get(int msw, int lsw)
    {
        int acx = msw >= 0 ? msw + FLAT_LOOKUP_SIZE_DIVIDED_BY_2 : msw * -1;
        int acz = lsw >= 0 ? lsw + FLAT_LOOKUP_SIZE_DIVIDED_BY_2 : lsw * -1;
        if (acx < FLAT_LOOKUP_SIZE && acz < FLAT_LOOKUP_SIZE) {
            return (V) flatLookup[(acx * FLAT_LOOKUP_SIZE) + acz];
        } else {
            return null;
        }
    }

    public V get(long key)
    {
        return get( LongHash.msw( key ), LongHash.lsw( key ) );
    }
}
