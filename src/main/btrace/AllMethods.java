
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.sun.btrace.AnyType;
import com.sun.btrace.annotations.*;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import static com.sun.btrace.BTraceUtils.println;


@BTrace(unsafe = true)
public class AllMethods {

    public static File outputDir;

    private static ThreadLocal<File> outputFile = new ThreadLocal<File>();

    private static long zero = new Date().getTime();

    static {
        outputDir = new File("/tmp/btrace");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
    }

    public AllMethods() {

    }

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
            String messageName = "" + (new Date().getTime() - zero) + "-" + getMethodName.invoke(header);
            println(messageName);
            outputFile.set(new File(outputDir, messageName));
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


            StringBuilder output = new StringBuilder();
            Descriptors.MethodDescriptor descriptor = (Descriptors.MethodDescriptor) (Object) args[0];
            output.append("----" + descriptor.getService().getName() + ":" + descriptor.getName() + " (" + descriptor.getInputType().getName() + ") : " + descriptor.getOutputType().getName() + "\n");
            Message message = (Message) (Object) args[2];
            output.append(message.toString() + "\n");
            output.append("---------------" + Thread.currentThread().getId() + "\n");
            writeToFile(output.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void writeToFile(String s) {
        File file = outputFile.get();
        try {
            FileWriter writer = new FileWriter(file, true);
            writer.write(s);
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    @OnMethod(
            clazz = "+com.google.protobuf.BlockingService",
            method = "/callBlockingMethod/",
            location = @Location(Kind.RETURN)
    )
    public static void serviceCall(@Self Object o, @ProbeClassName String probeClass, @ProbeMethodName String probeMethod, @Return Object result) {
        try {
            StringBuilder output = new StringBuilder()`;
            output.append("==== " + o.getClass() + "\n");
            Message message = (Message) (Object) result;
            output.append(message.toString() + "\n");
            output.append("========" + Thread.currentThread().getId() + "\n");
            writeToFile(output.toString());
            outputFile.remove();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}