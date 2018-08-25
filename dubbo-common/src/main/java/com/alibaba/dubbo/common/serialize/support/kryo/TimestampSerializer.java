package com.alibaba.dubbo.common.serialize.support.kryo;

import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.javakaffee.kryoserializers.KryoReflectionFactorySupport;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;

import java.sql.Timestamp;

public class TimestampSerializer  extends Serializer<Timestamp> {

    @Override
    public void write(Kryo kryo, Output output, Timestamp object) {
        output.writeLong(object.getTime(), true);
    }

    @Override
    public Timestamp read(Kryo kryo, Input input, Class<Timestamp> type) {
        return new Timestamp(input.readLong(true));
    }
    public Timestamp copy(Kryo kryo, Timestamp original) {
        return new Timestamp(original.getTime());
    }
}
