import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MyTest {
    public static void main(String[] args) throws Exception {
        String beniPath = "data/samples/benign";
        String malPath = "data/samples/malicious";
        int threadhold = 10;
        CreateDict temp = new CreateDict(beniPath, malPath, threadhold);
        temp.createDict();
//        temp.getDict("benign_dic");
//        temp.getDict("malicious_dic.json");
    }
}