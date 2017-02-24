import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.sun.btrace.AnyType;
import com.sun.btrace.annotations.*;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOpCodes;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;

import static com.sun.btrace.BTraceUtils.println;


@BTrace(unsafe = true)
public class FsEditLog {

    public static File outputDir;

    private static ThreadLocal<File> outputFile = new ThreadLocal<File>();

    private static long zero = new Date().getTime();

    static {
        outputDir = new File("/tmp/btrace");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
    }

    public FsEditLog() {

    }

    @OnMethod(
            clazz = "org.apache.hadoop.hdfs.server.namenode.FsEditLog",
        method = "/doEditTransaction/",
        location = @Location(Kind.RETURN)
    )
    public static void serviceCall(@Self Object o, @ProbeClassName String probeClass, @ProbeMethodName String probeMethod, @Return Object result) {
        try {
            FSEditLogOp editLogOp = (FSEditLogOp) result;
            println(editLogOp.getTransactionIdStr()+" "+ editLogOp.opCode.name());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}