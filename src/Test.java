import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 查看dict这个字典里面的内容
 * 与PE文件提取的3-grams序列进行对比
 *
 * @author fangzhiyang
 */
public class Test {
    public static void main(String[] args) throws Exception {
        File dict = new File("data/dict");
        File sample = new File("data/Backdoor.Win32.Hupigon.zay");

        ObjectInputStream ii = new ObjectInputStream(new FileInputStream(dict));
        Map<String, Integer> map = (Map<String, Integer>) ii.readObject();
        Iterator<String> it = map.keySet().iterator();

        int num = 0;
        while (it.hasNext()) {
            String key = it.next();
            if (key.equals("number")) {
                num = map.get(key);
                break;
            }
        }

        // 统计3-grams的每种序列出现的次数
        Map<String, Integer> singleMap = new HashMap<>();

        int maxcount = 0;
        String first = "", second = "", third = "", skey = "";
        int j = 0;

        DataInputStream in = new DataInputStream(new FileInputStream(sample));
        byte tempbyte = 0;

        for (int i = 0; i < sample.length(); i++) {
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

        StringBuilder result = new StringBuilder();

        while (it.hasNext()) {
            String key = it.next();
            if (key.equals("number"))
                continue;
            double tf = 0;
            if (singleMap.containsKey(key))
                tf = singleMap.get(key);
            double df = map.get(key);

            long tfidf = Math.round((tf / maxcount) * Math.log(num / (1 + df)) * 10000000);
            System.out.println(tf + "->" + df + "->" + key + "->" + tfidf);
            result.append(tfidf).append(",");
        }

        System.out.println(result);
    }
}
