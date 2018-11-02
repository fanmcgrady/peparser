import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import java.util.Map;

public class CreateDict {
    int threshold = 0;

    Map<String, Integer> beniMap = new HashMap<String, Integer>();
    Map<String, Integer> malMap = new HashMap<String, Integer>();
    Map<String, Integer> map = new HashMap<String, Integer>();

    String beniName = "benign_dic.json";
    String malName = "malicious_dic.json";

    String beniPath = "";
    String malPath = "";

    CreateDict(String beniPath, String malPath, int threashold) {
        this.beniPath = beniPath;
        this.malPath = malPath;
        this.threshold = threashold;
    }

    public void createDict() throws Exception {
        String[] path = new String[]{beniPath, malPath};
        for (int i = 0; i < path.length; i++) {
            File root = new File(path[i]);
            File[] files = root.listFiles();
            int count = 0;
            for (File file : files) {
                System.out.println(count++);
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
                    if (i == 0) {
                        if (!beniMap.containsKey(skey)) {
                            beniMap.put(skey, 1);
                        } else {
                            beniMap.put(skey, beniMap.get(skey) + 1);
                        }
                    } else {
                        if (!malMap.containsKey(skey)) {
                            malMap.put(skey, 1);
                        } else {
                            malMap.put(skey, malMap.get(skey) + 1);
                        }
                    }

                }
                in.close();
            }
        }

//        filter();
        saveDict();
    }

    public void statistics () throws Exception{
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
        BufferedWriter bw = null;
        File file = new File("statistics/" + df.format(new Date()) + ".txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void filter() {
        Iterator<Map.Entry<String, Integer>> beniIt = beniMap.entrySet().iterator();
        while (beniIt.hasNext()) {
            Map.Entry<String, Integer> entry = beniIt.next();
            String key = (String) entry.getKey();
            int beniValue = beniMap.get(key);
            if (malMap.containsKey(key)) {
                int malValue = malMap.get(key);
                if (beniValue >= malValue) {
                    malMap.remove(key);
                    if (shouldRemove(beniValue, malValue)) {
                        beniIt.remove();
                    }
                } else {
                    beniIt.remove();
                    if (shouldRemove(malValue, beniValue)) {
                        malMap.remove(key);
                    }
                }
            } else {
                if (shouldRemove(beniValue, 0)) {
                    beniIt.remove();
                }
            }
        }

        Iterator<Map.Entry<String, Integer>> malIt = malMap.entrySet().iterator();
        while (malIt.hasNext()) {
            Map.Entry<String, Integer> entry = malIt.next();
            String key = (String) entry.getKey();
            if (malMap.get(key) < threshold) {
                malIt.remove();
            }

        }
    }

    public void saveDict() throws Exception {
        File file = new File(beniName);
        if (!file.exists()) {
            file.createNewFile();
        }
        String beniJson = JSONObject.toJSONString(beniMap);

        FileWriter fw = new FileWriter(file);
        BufferedWriter out = new BufferedWriter(fw);
        out.write(beniJson);
        out.close();

        File file2 = new File(malName);
        if (!file2.exists()) {
            file2.createNewFile();
        }
        String malJson = JSONObject.toJSONString(malMap);

        FileWriter fw2 = new FileWriter(file2);
        BufferedWriter out2 = new BufferedWriter(fw2);
        out2.write(malJson);
        out2.close();

    }

    public void getDict(String filename) throws Exception {
        File file = new File(filename);
        String jsonContent = FileUtils.readFileToString(file, "UTF-8");
//        System.out.println(jsonContent);

        map = (Map) JSONObject.parse(jsonContent);

//        Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
//        int max = 0;
//        while (it.hasNext()) {
//            Map.Entry<String, Integer> entry = it.next();
//            if (max < entry.getValue()) {
//                max = entry.getValue();
//            }
//        }
    }

    public int calDistribution(String filename) throws Exception {
        File file = new File(filename);
        String jsonContent = FileUtils.readFileToString(file, "UTF-8");

        map = (Map) JSONObject.parse(jsonContent);

//        int distribution[] = new int[];

        Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
        int max = 0;
        int count = 0;
        while (it.hasNext()) {
            System.out.println(count++);
            Map.Entry<String, Integer> entry = it.next();
            if (max < entry.getValue()) {
                max = entry.getValue();
            }
        }
        return max;
    }

    public boolean shouldRemove(int value1, int value2) {
        if (value1 - value2 < threshold) {
            return true;
        }
        return false;
    }
}
