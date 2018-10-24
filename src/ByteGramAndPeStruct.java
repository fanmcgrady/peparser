import java.io.*;
import java.util.*;

public class ByteGramAndPeStruct {
    public int k = 0;
    int num = 0;
    Map<String, Integer> map = new HashMap<String, Integer>();
    Map<String, Integer> singleMap = new HashMap<String, Integer>();

    public boolean isLoadDict() {
        return !map.isEmpty();
    }

    public Boolean createSingleArff(String filename) {
        if (!isLoadDict()) {
            return false;
        }

        String arffName = filename + ".arff";
        try {
            File file = new File(filename);
            if (FileType.isEXE(file.getAbsolutePath())) {
                File arfffile = new File(arffName);
                if (!arfffile.exists())
                    arfffile.createNewFile();
                FileWriter fw = new FileWriter(arfffile.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(createHeader());
                bw.write(createFeature(file.getAbsolutePath()) + "malware\r\n");
                bw.close();

            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean createDict(String Path) {
        try {
            File root = new File(Path);
            File[] files = root.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    // if(!file.getAbsolutePath().startsWith("/home/bjr"))
                    createDict(file.getAbsolutePath());
                } else {
                    // System.out.println(file.getAbsolutePath());
                    if (FileType.isEXE(file.getAbsolutePath())) {
                        k++;
                        System.out.println("count->" + k);
                        singleMap.clear();
                        String first = "", second = "", third = "", skey = "";
                        int j = 0;

                        // �?次读�?个字�?
                        DataInputStream in = new DataInputStream(new FileInputStream(file));
                        byte tempbyte = 0;

                        for (int i = 0; i < file.length(); i++) {
                            tempbyte = in.readByte();
                            String str = Integer.toHexString(tempbyte);
                            if (str.length() == 8) {// 去掉补位的f
                                str = str.substring(6);
                            }
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
                                singleMap.put(skey, 1);
                                if (map.containsKey(skey)) {
                                    map.put(skey, map.get(skey) + 1);
                                } else {
                                    map.put(skey, 1);
                                }
                            }
                        }
                        in.close();
                    }

                }
            }
            map.put("number", k);
            // if (map.containsKey("number")){
            // map.put("number",k);
            // }else{
            // map.put("number", k);
            // }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void refixArff(String file) {
        File f = new File(file);
        try {
            InputStreamReader read = new InputStreamReader(new FileInputStream(file));
            BufferedReader bufferedReader = new BufferedReader(read);
            FileWriter fileWritter = new FileWriter(file + ".txt");
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);

            String line = null;
            int i = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("@attribute")) {
                    ++i;
                } else if (line.startsWith("@data")) {
                    bufferWritter.write(line + "\r\n");
                    bufferWritter.flush();
                    break;
                }
                bufferWritter.write(line + "\r\n");
                bufferWritter.flush();
            }
            String str[];
            while ((line = bufferedReader.readLine()) != null) {
                str = line.split(",");
                if (str.length == i) {
                    bufferWritter.write(line + "\r\n");
                } else if (str.length + 201 == i) {
                    line = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,"
                            + "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,"
                            + "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,"
                            + "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,"
                            + "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,"
                            + "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,"
                            + "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0," + line;
                    bufferWritter.write(line + "\r\n");
                }
                bufferWritter.flush();
            }
            read.close();
            bufferWritter.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public String createFeature(String filename) {
        StringBuilder result = new StringBuilder();
        // pe结构特征
//        String pe = peFile.createPEarff(new PyString(filename));
//        if (pe == null || pe.isEmpty()) {
        result.append("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0," + "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0," + "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0," + "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0," + "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0," + "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0," + "0,0,0,0,0,0,0,0,0,");
//        } else {
//            result.append(pe);
//        }
        System.out.println("result" + result);
        // gram
        try {
            File file = new File(filename);

            int maxcount = 0;
            singleMap.clear();
            String first = "", second = "", third = "", skey = "";
            int j = 0;

            DataInputStream in = new DataInputStream(new FileInputStream(file));
            byte tempbyte = 0;

            for (int i = 0; i < file.length(); i++) {
                tempbyte = in.readByte();
                String str = Integer.toHexString(tempbyte);
                if (str.length() == 8) {// 去掉补位的f
                    str = str.substring(6);
                }
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
                    int tcount = singleMap.get(skey) + 1;
                    singleMap.put(skey, tcount);
                    if (tcount > maxcount)
                        maxcount = tcount;
                } else {
                    singleMap.put(skey, 1);
                }
            }
            in.close();
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if (key.equals("number"))
                    continue;
                double tf = 0;
                if (singleMap.containsKey(key))
                    tf = singleMap.get(key);
                double df = map.get(key);
                // System.out.println(tf+"->"+df);

                long tfidf = Math.round((tf / maxcount) * Math.log(num / (1 + df)) * 10000000);
                // System.out.println(tf+"->"+df+"->"+key+"->"+tfidf);
                result.append(tfidf).append(",");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        return result.toString();
    }

    public String createHeader() {
        String result = "@relation malware\r\n";
        // pe结构
        for (int i = 0; i < 201; ++i) {
            result += "@attribute a" + i + " real\r\n";
        }
        // 字典结构
        return result + createByteHeader() + "@attribute result{malware,benign}\r\n" + "@data\r\n";
    }

    public String createByteHeader() {
        String result = "";
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (key.equals("number"))
                continue;
            result = result + "@attribute " + key + " numeric\r\n";
        }
        return result;
    }

    public Boolean createFromPath(BufferedWriter bw, String Path, String clable) {
        try {
            int i = 0;
            File root = new File(Path);
            File[] files = root.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    // if(!file.getAbsolutePath().startsWith("/home/bjr"))
                    createFromPath(bw, file.getAbsolutePath(), clable);
                } else {
                    if (FileType.isEXE(file.getAbsolutePath())) {
                        bw.write(createFeature(file.getAbsolutePath()) + clable + "\r\n");
                        System.out.println(clable + "->" + (++k));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean createTrainset(String benPath, String malPath, String arffName) {
        if (!isLoadDict()) {
            System.out.println("there is no dict.");
            return false;
        }
        try {
            k = 0;
            File file = new File(arffName);
            if (!file.exists())
                file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(createHeader());
            createFromPath(bw, benPath, "benign");
            createFromPath(bw, malPath, "malware");

            bw.close();
        } catch (IOException ex) {
            System.out.println(ex.getStackTrace());
            return false;
        }
        return true;
    }

    public void printdict() {

        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            System.out.println("key:" + key);
            System.out.println("value:" + map.get(key));
        }
    }

    public Boolean filterDict(int count) {
        try {
            List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return (o2.getValue() - o1.getValue());
                }
            });

            map.clear();
            // 排序�?
            for (int i = 0; i < count; i++) {
                String id = list.get(i).toString();
                String tmp[] = id.split("=");
                map.put(tmp[0], new Integer(tmp[1]));
                // System.out.println(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean dict2file(String filename) {
        try {

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
            out.writeObject(map); //
            System.out.println("object has been written..");
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public Boolean file2dict(String filename) {
        if (filename == null || !(new File(filename)).isFile()) {
            return false;
        }
        try {
            map.clear();
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
            map = (Map<String, Integer>) in.readObject();
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if (key.equals("number")) {
                    num = (int) map.get(key);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
