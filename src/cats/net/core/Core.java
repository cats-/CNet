package cats.net.core;

import cats.net.core.buffer.Buffer;
import cats.net.core.buffer.BufferBuilder;
import cats.net.core.connection.utils.ConnectionUtils;
import cats.net.core.data.former.DataFormer;
import cats.net.core.decode.Decoder;
import cats.net.core.encode.Encoder;
import cats.net.core.utils.CoreUtils;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class Core {

    private static final Map<String, Encoder> ENCODERS = new HashMap<>();
    private static final Map<String, Decoder> DECODERS = new HashMap<>();

    private static final Map<short[], DataFormer> DATA_FORMERS = new HashMap<>();

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
        add(BufferedImage.class,
                (Encoder<BufferedImage>) (bldr, img) -> {
                    final int width = img.getWidth();
                    final int height = img.getHeight();
                    bldr.putInt(width).putInt(height).putInt(img.getType());
                    for(int r = 0; r < height; r++)
                        for(int c = 0; c < width; c++)
                            bldr.putInt(img.getRGB(r, c));
                },
                (Decoder<BufferedImage>) buf -> {
                    final int width = buf.getInt();
                    final int height = buf.getInt();
                    final BufferedImage img = new BufferedImage(width, height, buf.getInt());
                    for(int r = 0; r < height; r++)
                        for(int c = 0; c < width; c++)
                            img.setRGB(r, c, buf.getInt());
                    return img;
                }
        );
        add(Color.class,
                (Encoder<Color>) (bldr, col) -> bldr.putInt(col.getRGB()),
                (Decoder<Color>) buf -> new Color(buf.getInt())
        );
        add(Point.class,
                (Encoder<Point>) (bldr, pt) -> bldr.putInt(pt.x).putInt(pt.y),
                (Decoder<Point>) buf -> new Point(buf.getInt(), buf.getInt())
        );
        add(Rectangle.class,
                (Encoder<Rectangle>) (bldr, rect) ->
                    bldr.putInt(rect.x).putInt(rect.y).putInt(rect.width).putInt(rect.height),
                (Decoder<Rectangle>) buf -> new Rectangle(buf.getInt(), buf.getInt(), buf.getInt(), buf.getInt())
        );
    }

    private Core(){}

    public static <T> void add(final Class<T> clazz, final Encoder<T> encoder, final Decoder<T> decoder){
        add(clazz.getName(), encoder, decoder);
    }

    public static <T> void add(final String name, final Encoder<T> encoder, final Decoder<T> decoder){
        ENCODERS.put(name, encoder);
        DECODERS.put(name, decoder);
    }

    public static void addDataFormer(final DataFormer former){
        DATA_FORMERS.put(former.getOpcodes(), former);
        CoreUtils.print("Registered former %s at opcodes: %s", former.getClass(), Arrays.toString(former.getOpcodes()));
    }

    public static boolean addDataFormers(final InputStream input){
        try{
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document document = builder.parse(input);
            document.getDocumentElement().normalize();
            final NodeList handlers = document.getElementsByTagName("former");
            for(int i = 0; i < handlers.getLength(); i++){
                final Node node = handlers.item(i);
                if(node.getNodeType() != Node.ELEMENT_NODE)
                    continue;
                final Element element = (Element)node;
                final Class<? extends DataFormer> clazz = (Class<? extends DataFormer>)Class.forName(element.getTextContent());
                addDataFormer(clazz.newInstance());
            }
            ConnectionUtils.close(input);
            return true;
        }catch(Exception ex){
            CoreUtils.print(ex);
            return false;
        }
    }

    public static boolean addDataFormers(final File xml){
        try{
            return addDataFormers(new FileInputStream(xml));
        }catch(Exception ex){
            CoreUtils.print(ex);
            return false;
        }
    }

    public static <T extends DataFormer> T getDataFormer(final short opcode){
        for(final short[] opcodes : DATA_FORMERS.keySet())
            for(final short o : opcodes)
                if(o == opcode)
                    return (T) DATA_FORMERS.get(opcodes);
        return null;
    }

    public static DataFormer getDataFormer(final int opcode){
        return getDataFormer((short)opcode);
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
