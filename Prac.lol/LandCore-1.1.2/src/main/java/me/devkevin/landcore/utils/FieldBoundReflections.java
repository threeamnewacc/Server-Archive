package me.devkevin.landcore.utils;

import org.apache.commons.lang.Validate;

import java.lang.reflect.Field;

public class FieldBoundReflections {

    private Field field;

    public FieldBoundReflections() {
    }

    public FieldBoundReflections(Field field) {
        this.field = field;
    }

    public static FieldBoundReflections access(Class<?> clazz, String name) {
        Validate.notNull(clazz);

        try {
            Field field = clazz.getDeclaredField(name);
            return access(field);
        } catch (NoSuchFieldException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static FieldBoundReflections access(Field field) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        return new FieldBoundReflections(field);
    }

    public FieldBoundReflections set(Object instance, Object value) {
        try {
            field.set(instance, value);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }

        return this;
    }

    public FieldBoundReflections setInt(Object instance, int value) {
        try {
            field.setInt(instance, value);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }

        return this;
    }

    public FieldBoundReflections setFloat(Object instance, float value) {
        try {
            field.setFloat(instance, value);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }

        return this;
    }

    public FieldBoundReflections setDouble(Object instance, double value) {
        try {
            field.setDouble(instance, value);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }

        return this;
    }

    public FieldBoundReflections setByte(Object instance, byte value) {
        try {
            field.setByte(instance, value);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }

        return this;
    }

    public FieldBoundReflections setLong(Object instance, long value) {
        try {
            field.setLong(instance, value);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }

        return this;
    }

    public FieldBoundReflections setShort(Object instance, short value) {
        try {
            field.setShort(instance, value);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }

        return this;
    }

    public FieldBoundReflections setChar(Object instance, char value) {
        try {
            field.setChar(instance, value);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }

        return this;
    }

    public FieldBoundReflections setBoolean(Object instance, boolean value) {
        try {
            field.setBoolean(instance, value);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }

        return this;
    }

    public <E> E get(Object instance) {
        try {
            return (E) field.get(instance);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    public int getInt(Object instance) {
        try {
            return field.getInt(instance);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    public double getDouble(Object instance) {
        try {
            return field.getDouble(instance);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    public float getFloat(Object instance) {
        try {
            return field.getFloat(instance);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    public byte getByte(Object instance) {
        try {
            return field.getByte(instance);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    public char getChar(Object instance) {
        try {
            return field.getChar(instance);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    public long getLong(Object instance) {
        try {
            return field.getLong(instance);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    public short getShort(Object instance) {
        try {
            return field.getShort(instance);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    public FieldBoundReflections bind(Field field) {
        this.field = field;
        return this;
    }

    public Field unbind() {
        Field oldField = field;
        this.field = null;
        return oldField;
    }

    public boolean isBound() {
        return this.field != null;
    }

}


