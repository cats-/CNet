package cats.net.core;

import cats.net.core.buffer.Buffer;
import cats.net.core.buffer.BufferBuilder;
import cats.net.core.decode.Decoder;
import cats.net.core.encode.Encoder;
import java.util.HashMap;
import java.util.Map;

public final class Core {

    private static final Map<String, Encoder> ENCODERS = new HashMap<>();
    private static final Map<String, Decoder> DECODERS = new HashMap<>();

    public static boolean verbose = true;

    public static int bufferSize = 1024;

    static{
        add(Boolean.class,
                (Encoder<Boolean>) BufferBuilder::putBoolean,
                (Decoder<Boolean>) Buffer::getBoolean);
        add(Byte.class,
                (Encoder<Byte>) BufferBuilder::putByte,
                (Decoder<Byte>) Buffer::getByte);
        add(Character.class,
                (Encoder<Character>) BufferBuilder::putChar,
                (Decoder<Character>) Buffer::getChar);
        add(Short.class,
                (Encoder<Short>) BufferBuilder::putShort,
                (Decoder<Short>) Buffer::getShort);
        add(Integer.class,
                (Encoder<Integer>) BufferBuilder::putInt,
                (Decoder<Integer>) Buffer::getInt);
        add(Float.class,
                (Encoder<Float>) BufferBuilder::putFloat,
                (Decoder<Float>) Buffer::getFloat);
        add(Double.class,
                (Encoder<Double>) BufferBuilder::putDouble,
                (Decoder<Double>) Buffer::getDouble);
        add(Long.class,
                (Encoder<Long>) BufferBuilder::putLong,
                (Decoder<Long>) Buffer::getLong);
        add(String.class,
                (Encoder<String>) BufferBuilder::putString,
                (Decoder<String>) Buffer::getString);
    }

    private Core(){}

    public static <T> void add(final Class<T> clazz, final Encoder<T> encoder, final Decoder<T> decoder){
        add(clazz.getName(), encoder, decoder);
    }

    public static <T> void add(final String name, final Encoder<T> encoder, final Decoder<T> decoder){
        ENCODERS.put(name, encoder);
        DECODERS.put(name, decoder);
    }

    public static Encoder getEncoder(final Class clazz){
        return getEncoder(clazz.getName());
    }

    public static Encoder getEncoder(final String name){
        return ENCODERS.get(name);
    }

    public static Decoder getDecoder(final Class clazz){
        return getDecoder(clazz.getName());
    }

    public static Decoder getDecoder(final String name){
        return DECODERS.get(name);
    }
}
