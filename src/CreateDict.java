import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class CreateDict {
    int threshold = 0;
    int number = 215;
    int count = 1000;

    Map<String, Integer> beniMap = new HashMap<String, Integer>();
    Map<String, Integer> malMap = new HashMap<String, Integer>();
    Map<String, Integer> singleMap = new HashMap<String, Integer>();
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
                singleMap.clear();
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
                    if (!singleMap.containsKey(skey)) {
                        singleMap.put(skey,1);
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
                }
                in.close();
            }
        }
        filter();
        saveDict();
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
//                if (shouldRemove(beniValue, 0)) {
//                    beniIt.remove();
//                }
            }
        }

        Iterator<Map.Entry<String, Integer>> malIt = malMap.entrySet().iterator();
//        while (malIt.hasNext()) {
//            Map.Entry<String, Integer> entry = malIt.next();
//            String key = (String) entry.getKey();
//            if (shouldRemove(malMap.get(key),0)) {
//                malIt.remove();
//            }
//        }

        try {
            List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(beniMap.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return (o2.getValue() - o1.getValue());
                }
            });

            beniMap.clear();
            // 排序�?
            for (int i = 0; i < count; i++) {
                String id = list.get(i).toString();
                String tmp[] = id.split("=");
                beniMap.put(tmp[0], new Integer(tmp[1]));
                // System.out.println(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(malMap.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return (o2.getValue() - o1.getValue());
                }
            });

            malMap.clear();
            // 排序�?
            for (int i = 0; i < count; i++) {
                String id = list.get(i).toString();
                String tmp[] = id.split("=");
                malMap.put(tmp[0], new Integer(tmp[1]));
                // System.out.println(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        map = (Map) JSONObject.parse(jsonContent);
    }

    public int statistics(String filename, String type) throws Exception {
        File file = new File(filename);
        String jsonContent = FileUtils.readFileToString(file, "UTF-8");

        map = (Map) JSONObject.parse(jsonContent);

        int distribution[] = new int[number+1];
        for (int i = 0; i < distribution.length; i++) {
            distribution[i] = 0;
        }

        Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
        int max = 0;
        int count = 0;
        int countInStatistics = 0;
        int length = 0;
        while (it.hasNext()) {
            count++;

            Map.Entry<String, Integer> entry = it.next();
            int value = entry.getValue();
            length += value;
            int pos = value;
            if (pos <= number) {
                distribution[pos]++;
                countInStatistics++;
            }
            if (max < value) {
                max = value;
            }
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
        BufferedWriter bw = null;
        File f = new File("statistics/" + type + "-" + df.format(new Date()) + ".txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileWriter fw = new FileWriter(f.getAbsoluteFile());
            bw = new BufferedWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bw.write("count = " + count + "\n");
        bw.write("max = " + max + "\n");

        for (int i = 0; i < distribution.length; i++) {
            if (distribution[i] != -1) {
                bw.write(i +": " + distribution[i] + "\n");
            }
        }
        bw.write("number of consideraion = " + countInStatistics+"\n");
        bw.write("sum of all value = " + length+"\n");
        bw.close();
        return max;
    }

    public boolean shouldRemove(int value1, int value2) {
        if ((value1 - value2) < number*0.2) {
            return true;
        }
        return false;
    }
}
