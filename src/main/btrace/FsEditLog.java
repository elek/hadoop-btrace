import com.sun.btrace.AnyType;
import com.sun.btrace.annotations.*;
import com.sun.xml.bind.marshaller.XMLWriter;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.StringWriter;
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
        clazz = "+org.apache.hadoop.hdfs.server.namenode.EditLogOutputStream",
        method = "/write/"
    )
    public static void write(@Self Object o, @ProbeClassName String probeClass, @ProbeMethodName String probeMethod, AnyType[] args) {
        FSEditLogOp editLogOp = (FSEditLogOp) args[0];
        println("Writing " + editLogOp.getClass().getSimpleName() + " to the journal log with" + probeClass);
        try {
            StringWriter buffer = new StringWriter();
            XMLWriter xwriter = new XMLWriter(buffer, "UTF-8");
            editLogOp.outputToXml(xwriter);
            println(buffer.toString());
            println();
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }

}