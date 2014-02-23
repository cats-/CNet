package cats.net.core.data.former;

import cats.net.core.Core;
import cats.net.core.data.Data;
import cats.net.core.utils.CoreUtils;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;

public abstract class DataFormer {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Former{}

    protected Data data;

    public abstract short[] getOpcodes();

    public final Data form(final short opcode, final Object... args){
        try{
           final Method method = Arrays.stream(getClass().getDeclaredMethods()).filter(
                   m -> m.isAnnotationPresent(Former.class)
                           && matches(args, m.getParameterTypes())
                           && m.getReturnType().equals(Data.class)
           ).findFirst().orElse(null);
            CoreUtils.print("data former method for opcode %d: %s", opcode, method);
           if(method == null)
               return null;
           if(!method.isAccessible())
               method.setAccessible(true);
            data = new Data(opcode);
            return (Data)method.invoke(this, args);
        }catch(Exception ex){
            CoreUtils.print(ex);
            return null;
        }
    }

    private static Class[] types(final Object... args){
        final Class[] types = new Class[args.length];
        for(int i = 0; i < args.length; i++)
            types[i] = args[i].getClass();
        return types;
    }

    private static boolean matches(final Object[] args, final Class[] paramTypes){
        final Class[] argTypes = types(args);
        if(Arrays.equals(argTypes, paramTypes))
            return true;
        for(int i = 0; i < argTypes.length; i++)
            if(!argTypes[i].isAssignableFrom(paramTypes[i]) && !argTypes[i].equals(paramTypes[i]))
                return false;
        return true;
    }
}
