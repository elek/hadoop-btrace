
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.sun.btrace.AnyType;
import com.sun.btrace.annotations.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.sun.btrace.BTraceUtils.println;


@BTrace(unsafe = true)
public class AllMethods {
    @OnMethod(
            clazz = "/org\\.apache\\.hadoop\\.ipc\\..*ProtoBufRpcInvoker.*/",
            method = "/call/"
    )
    public static void rpcCall(@Self Object o, @ProbeClassName String probeClass, @ProbeMethodName String probeMethod, AnyType[] args) {
        try {
            Field requestHeader = args[2].getClass().getSuperclass().getDeclaredField("requestHeader");
            requestHeader.setAccessible(true);
            Object header = requestHeader.get(args[2]);
            Method getMethodName = header.getClass().getMethod("getMethodName");
            println(getMethodName.invoke(header));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnMethod(
            clazz = "+com.google.protobuf.BlockingService",
            method = "/callBlockingMethod/"
    )
    public static void serviceCallReturn(@Self Object o, @ProbeClassName String probeClass, @ProbeMethodName String probeMethod, AnyType[] args) {
        try {

            Descriptors.MethodDescriptor descriptor = (Descriptors.MethodDescriptor) (Object) args[0];
            println("----" + descriptor.getService().getName() + ":" + descriptor.getName() + " (" + descriptor.getInputType().getName() + ") : " + descriptor.getOutputType().getName());
            Message message = (Message) (Object) args[2];
            println(message.toString());
            println("---------------" + Thread.currentThread().getId());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @OnMethod(
            clazz = "+com.google.protobuf.BlockingService",
            method = "/callBlockingMethod/",
            location = @Location(Kind.RETURN)
    )
    public static void serviceCall(@Self Object o, @ProbeClassName String probeClass, @ProbeMethodName String probeMethod, @Return Object result) {
        try {
            println("==== " + o.getClass());
            Message message = (Message) (Object) result;
            println(message.toString());
            println("========" + Thread.currentThread().getId());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//
//    @OnMethod(
//            clazz = "/org\\.apache\\.hadoop\\.ipc\\..*ProtoBufRpcInvoker.*/",
//            method = "/call/",
//            location = @Location(Kind.RETURN)
//    )
//    public static void rpcReturn(@Self Object o, @ProbeClassName String probeClass, @ProbeMethodName String probeMethod, @Return Object result) {
//        try {
//            println("result " + result);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }


}