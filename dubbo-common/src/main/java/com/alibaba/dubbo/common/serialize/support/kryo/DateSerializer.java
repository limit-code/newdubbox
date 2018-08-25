package com.alibaba.dubbo.common.serialize.support.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.lang.reflect.Constructor;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class DateSerializer extends Serializer<Date> {
    @Override
    public void write(Kryo kryo, Output output, Date object) {
        output.writeLong(object.getTime(), true);
    }

    @Override
    public Date read(Kryo kryo, Input input, Class<Date> type) {
        return create(kryo, type, input.readLong(true));
    }
    public Date copy (Kryo kryo, Date original) {
        return create(kryo, original.getClass(), original.getTime());
    }

    private Date create(Kryo kryo, Class<?> type, long time) throws KryoException {
        if (type.equals(Date.class)) {
            return new Date(time);
        }
//        if (type.equals(Timestamp.class)) {
//            return new Timestamp(time);
//        }
        if (type.equals(java.sql.Date.class)) {
            return new java.sql.Date(time);
        }
//        if (type.equals(Time.class)) {
//            return new Time(time);
//        }
        // other cases, reflection
        try {
            // Try to avoid invoking the no-args constructor
            // (which is expected to initialize the instance with the current time)
            Constructor constructor = type.getDeclaredConstructor(long.class);
            if (constructor!=null) {
                if (!constructor.isAccessible()) {
                    try {
                        constructor.setAccessible(true);
                    }
                    catch (Throwable t) {}
                }
                return (Date)constructor.newInstance(time);
            }
            else {
                Date d = (Date)kryo.newInstance(type); // default strategy
                d.setTime(time);
                return d;
            }
        } catch (Exception ex) {
            throw new KryoException(ex);
        }
    }
}
