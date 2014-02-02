package cats.net.core.encode;

import cats.net.core.buffer.BufferBuilder;

public interface Encoder<T> {

    public void encode(final BufferBuilder builder, final T obj);
}
