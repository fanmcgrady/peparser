import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;

public class CreateArff {
    String beniPath = "";
    String malPath = "";

    String beniDict = "";
    String malDict = "";

    Map<String, Integer> singleMap = new HashMap<String, Integer>();
    Map<String, Integer> beniMap = new HashMap<String, Integer>();
    Map<String, Integer> malMap = new HashMap<String, Integer>();

    CreateArff(String beniPath, String malPath, String beniDict, String malDict) {
        this.beniPath = beniPath;
        this.malPath = malPath;
        this.beniDict = beniDict;
        this.malDict = malDict;

        File file = new File(beniDict);
        try {
            String jsonContent = FileUtils.readFileToString(file, "UTF-8");
            beniMap = (Map) JSONObject.parse(jsonContent);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        file = new File(malDict);
        try {
            String jsonContent = FileUtils.readFileToString(file, "UTF-8");
            malMap = (Map) JSONObject.parse(jsonContent);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    public void start() throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String arffName = df.format(new Date()) + ".arff";
        File arfffile = new File(arffName);
        if (!arfffile.exists())
            arfffile.createNewFile();
        FileWriter fw = new FileWriter(arfffile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(createAttributes());
        bw.write("@data\r\n");

        String[] path = new String[]{beniPath, malPath};
        for (int i = 0; i < path.length; i++) {
            File root = new File(path[i]);
            File[] files = root.listFiles();
            int count = 0;
            for (File file : files) {
                System.out.println(count++);
                singleMap.clear();
                createSingleDict(file);
                bw.write(createSingleArff(i));
            }
        }
    }
    public String createAttributes(){
        StringBuilder result = new StringBuilder();
        Iterator<Map.Entry<String, Integer>> beniIt = beniMap.entrySet().iterator();
        while (beniIt.hasNext()) {
            Map.Entry<String, Integer> entry = beniIt.next();
            String key = (String) entry.getKey();
            result.append("@attribute ").append(key).append(" numeric\r\n");
        }

        Iterator<Map.Entry<String, Integer>> malIt = beniMap.entrySet().iterator();
        while (malIt.hasNext()) {
            Map.Entry<String, Integer> entry = malIt.next();
            String key = (String) entry.getKey();
            result.append("@attribute ").append(key).append(" numeric\r\n");
        }
        return result.toString();
    }

    public void createSingleDict(File file) throws Exception {
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        byte temp = 0;
        String first = "", second = "", third = "", skey = "";
        int j = 0;

        for (int k = 0; k < file.length(); k++) {
            temp = in.readByte();
            String str = Integer.toHexString(temp & 0xff);
            if (str.length() == 1)
                str = "0" + str;
            str = str.toUpperCase();

            if (j == 0) {
                third = str;
                j++;
                continue;
            } else if (j == 1) {
                second = third;
                third = str;
                j++;
                continue;
            } else if (j >= 2) {
                first = second;
                second = third;
                third = str;
            }
            skey = first + second + third;
            if (singleMap.containsKey(skey)) {
                singleMap.put(skey, singleMap.get(skey) + 1);
            } else {
                singleMap.put(skey, 1);
            }
        }
        in.close();
    }

    public String createSingleArff(int type) {   //type=0:benign  type=1:malicious
        StringBuilder result = new StringBuilder();
        Iterator<Map.Entry<String, Integer>> beniIt = beniMap.entrySet().iterator();
        while (beniIt.hasNext()) {
            Map.Entry<String, Integer> entry = beniIt.next();
            String key = (String) entry.getKey();
            int value = 0;
            if (singleMap.containsKey(key)) {
                value = singleMap.get(key);
            }
            value = calTfIdf(value, entry.getValue(), 1); // 1:benign  -1:malicious
            result.append(value).append(",");
        }

        Iterator<Map.Entry<String, Integer>> malIt = beniMap.entrySet().iterator();
        while (malIt.hasNext()) {
            Map.Entry<String, Integer> entry = malIt.next();
            String key = (String) entry.getKey();
            int value = 0;
            if (singleMap.containsKey(key)) {
                value = singleMap.get(key);
            }
            value = calTfIdf(value, entry.getValue(), -1); // 1:benign  -1:malicious
            result.append(value).append(",");
        }

        if (type == 0) {
            result.append("benign\r\n");
        } else {
            result.append("malicious\r\n");
        }
        return result.toString();
    }

    public int calTfIdf(int value, int df, int type) {
        return type * value * (10000 / df);
    }
}
