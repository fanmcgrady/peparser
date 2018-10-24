import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

/**
 * java调用python代码
 */
public class PeStruct {
    private PyObject peClass;

    public PeStruct() {
        try {
            PythonInterpreter interpreter = new PythonInterpreter();
            interpreter.exec("import sys");
            // tomcat作为服务器时需要使用完整路径
//			interpreter.exec("sys.path.append('" + SystemConfigure.get("jPythonHome") + "')");
            interpreter.exec("sys.path.append('./py')");
            interpreter.exec("from extractPE import extractPE");
            peClass = interpreter.get("extractPE");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public PeStructIml create() {
        PyObject peObject = peClass.__call__();
        return (PeStructIml) peObject.__tojava__(PeStructIml.class);
    }
}
