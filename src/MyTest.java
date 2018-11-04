import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MyTest {
    public static void main(String[] args) throws Exception {
        // the parameter "number" in CreateDict is equal to the sum of benign files.
        String beniPath = "data/samples/benign_small";
        String malPath = "data/samples/malicious_small";

        String beniDict = "benign_dic.json";
        String malDict = "malicious_dic.json";
        int threadhold = 0;
        CreateDict temp = new CreateDict(beniPath, malPath, threadhold);
        temp.createDict();
        temp.statistics("benign_dic.json","benign");
        temp.statistics("malicious_dic.json","malicious");

        CreateArff temp2 =new CreateArff(beniPath,malPath,beniDict,malDict);
        temp2.start();
    }
}
