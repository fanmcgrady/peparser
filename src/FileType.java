import java.io.*;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 获取文件类型，原来他们写的代码，较乱，未修改暂时能用
 *
 * @author fangzhiyang
 */
public class FileType {
    public final static Map<String, String> FILE_TYPE_MAP = new HashMap<String, String>();
    static String dst = "";
    static int count = 0;
    static int max = 0;

    static {
        getAllFileType();
    }

    private static void getAllFileType() {
        FILE_TYPE_MAP.put("ffd8ffe000104a464946", "jpg"); // JPEG (jpg)
        FILE_TYPE_MAP.put("89504e470d0a1a0a0000", "png"); // PNG (png)
        FILE_TYPE_MAP.put("47494638396126026f01", "gif"); // GIF (gif)
        FILE_TYPE_MAP.put("49492a00227105008037", "tif"); // TIFF (tif)
        FILE_TYPE_MAP.put("424d228c010000000000", "bmp"); // 16色位�?(bmp)
        FILE_TYPE_MAP.put("424d8240090000000000", "bmp"); // 24位位�?(bmp)
        FILE_TYPE_MAP.put("424d8e1b030000000000", "bmp"); // 256色位�?(bmp)
        FILE_TYPE_MAP.put("41433130313500000000", "dwg"); // CAD (dwg)
        FILE_TYPE_MAP.put("3c21444f435459504520", "html"); // HTML (html)
        FILE_TYPE_MAP.put("3c21646f637479706520", "htm"); // HTM (htm)
        FILE_TYPE_MAP.put("48544d4c207b0d0a0942", "css"); // css
        FILE_TYPE_MAP.put("696b2e71623d696b2e71", "js"); // js
        FILE_TYPE_MAP.put("7b5c727466315c616e73", "rtf"); // Rich Text Format
        FILE_TYPE_MAP.put("38425053000100000000", "psd"); // Photoshop (psd)
        FILE_TYPE_MAP.put("46726f6d3a203d3f6762", "eml"); // Email [Outlook
        FILE_TYPE_MAP.put("d0cf11e0a1b11ae10000", "doc"); // MS Excel
        FILE_TYPE_MAP.put("d0cf11e0a1b11ae10000", "vsd"); // Visio 锟斤拷图
        FILE_TYPE_MAP.put("5374616E64617264204A", "mdb"); // MS Access (mdb)
        FILE_TYPE_MAP.put("252150532D41646F6265", "ps");
        FILE_TYPE_MAP.put("255044462d312e350d0a", "pdf"); // Adobe Acrobat (pdf)
        FILE_TYPE_MAP.put("2e524d46000000120001", "rmvb"); // rmvb/rm锟斤拷同
        FILE_TYPE_MAP.put("464c5601050000000900", "flv"); // flv锟斤拷f4v锟斤拷同
        FILE_TYPE_MAP.put("00000020667479706d70", "mp4");
        FILE_TYPE_MAP.put("49443303000000002176", "mp3");
        FILE_TYPE_MAP.put("000001ba210001000180", "mpg"); //
        FILE_TYPE_MAP.put("3026b2758e66cf11a6d9", "wmv"); // wmv锟斤拷asf锟斤拷同
        FILE_TYPE_MAP.put("52494646e27807005741", "wav"); // Wave (wav)
        FILE_TYPE_MAP.put("52494646d07d60074156", "avi");
        FILE_TYPE_MAP.put("4d546864000000060001", "mid"); // MIDI (mid)
        FILE_TYPE_MAP.put("504b0304140000000800", "zip");
        FILE_TYPE_MAP.put("526172211a0700cf9073", "rar");
        FILE_TYPE_MAP.put("235468697320636f6e66", "ini");
        FILE_TYPE_MAP.put("504b03040a0000000000", "jar");

        FILE_TYPE_MAP.put("4d5a9000030000000400", "exe");// 锟斤拷执锟斤拷锟侥硷�?
        FILE_TYPE_MAP.put("4d5a5000020000000400", "exe");// 锟斤拷执锟斤拷锟侥硷�?
        FILE_TYPE_MAP.put("4d5a", "exe");// 锟斤拷执锟斤拷锟侥硷�?

        FILE_TYPE_MAP.put("3c25402070616765206c", "jsp");// jsp锟侥硷拷
        FILE_TYPE_MAP.put("4d616e69666573742d56", "mf");// MF锟侥硷拷
        FILE_TYPE_MAP.put("3c3f786d6c2076657273", "xml");// xml锟侥硷拷
        FILE_TYPE_MAP.put("494e5345525420494e54", "sql");// xml锟侥硷拷
        FILE_TYPE_MAP.put("7061636b616765207765", "java");// java锟侥硷拷
        FILE_TYPE_MAP.put("406563686f206f66660d", "bat");// bat锟侥硷拷
        FILE_TYPE_MAP.put("1f8b0800000000000000", "gz");// gz锟侥硷拷
        FILE_TYPE_MAP.put("6c6f67346a2e726f6f74", "properties");// bat锟侥硷拷
        FILE_TYPE_MAP.put("cafebabe0000002e0041", "class");// bat锟侥硷拷
        FILE_TYPE_MAP.put("49545346030000006000", "chm");// bat锟侥硷拷
        FILE_TYPE_MAP.put("04000000010000001300", "mxp");// bat锟侥硷拷
        FILE_TYPE_MAP.put("504b0304140006000800", "docx");// docx锟侥硷拷
        FILE_TYPE_MAP.put("d0cf11e0a1b11ae10000", "wps");// WPS锟斤拷锟斤拷wps锟斤拷锟斤拷锟絜t锟斤拷锟斤拷示dps锟斤拷锟斤拷�?锟斤拷锟斤拷
        FILE_TYPE_MAP.put("6431303a637265617465", "torrent");
        FILE_TYPE_MAP.put("6D6F6F76", "mov"); // Quicktime (mov)
        FILE_TYPE_MAP.put("FF575043", "wpd"); // WordPerfect (wpd)
        FILE_TYPE_MAP.put("CFAD12FEC5FD746F", "dbx"); // Outlook Express (dbx)
        FILE_TYPE_MAP.put("2142444E", "pst"); // Outlook (pst)
        FILE_TYPE_MAP.put("AC9EBD8F", "qdf"); // Quicken (qdf)
        FILE_TYPE_MAP.put("E3828596", "pwl"); // Windows Password (pwl)
        FILE_TYPE_MAP.put("2E7261FD", "ram"); // Real Audio (ram)
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 判断文件是否是pe文件
     */
    public static boolean isEXE(String filePath) {
        if (!new File(filePath).isFile()) {
            return false;
        }
        String fileCode = "";
        try {
            FileInputStream is = new FileInputStream(filePath);
            byte[] b = new byte[2];
            is.read(b, 0, b.length);
            fileCode = bytesToHexString(b).toLowerCase();
            if (fileCode == null) {
                return false;
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fileCode.equals("4d5a");
    }

    public static String getFileType(String filePath) {
        String res = "";
        try {
            FileInputStream is = new FileInputStream(filePath);
            byte[] b = new byte[10];
            is.read(b, 0, b.length);
            String fileCode = bytesToHexString(b);

            Iterator<String> keyIter = FILE_TYPE_MAP.keySet().iterator();
            while (keyIter.hasNext()) {
                String key = keyIter.next();
                if (key.toLowerCase().startsWith(fileCode.toLowerCase())
                        || fileCode.toLowerCase().startsWith(key.toLowerCase())) {
                    res = FILE_TYPE_MAP.get(key);
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void fileChannelCopy(File s, File t) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();// 锟矫碉拷锟斤拷应锟斤拷锟侥硷拷�?�锟斤拷
            out = fo.getChannel();// 锟矫碉拷锟斤拷应锟斤拷锟侥硷拷�?�锟斤拷
            in.transferTo(0, in.size(), out);// 锟斤拷锟斤拷锟斤拷锟斤拷通锟斤拷锟斤拷锟斤拷锟揭达拷in通锟斤拷锟斤拷取锟斤拷然锟斤拷写锟斤拷out通锟斤拷
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static void traverse(File file) {
        if (file.isDirectory() && count < max) {
            File list[] = file.listFiles();
            if (list == null || list.length == 0) {
                return;
            }
            for (File tmp : list) {
                if (tmp.isDirectory()) {
                    traverse(tmp);
                } else if (isEXE(tmp.getAbsolutePath()) && count < max) {
                    File f = new File(dst + tmp.getName());
                    if (!f.isFile()) {
                        fileChannelCopy(tmp, f);
                    }
                    ++count;
                }
            }
        } else if (isEXE(file.getAbsolutePath()) && count < max) {
            fileChannelCopy(file, new File(dst + file.getName()));
            ++count;
        }
    }

    public static void main(String[] args) {
        ByteGramAndPeStruct bs = new ByteGramAndPeStruct();
        ;
        try {
            if (args == null || args.length == 0) {
                System.out.println("createdict dictSrc, filterCount");
                System.out.println("createarff ben, mal");
                System.out.println("extractexe src dst number");
                System.out.println("continue addarff dir file label");
            } else if (args[0].equals("createdict")) {
                bs.createDict(args[1]);
                bs.filterDict(Integer.valueOf(args[2]));
                bs.dict2file("dict");
                System.out.println("created dict successfuly!");
            } else if (args[0].equals("createarff")) {
                bs.file2dict("dict");
                bs.createTrainset(args[1], args[2], "byte_pe.arff");
                System.out.println("created arff successfuly!");
            } else if (args[0].equals("extractexe")) {
                File dir = new File(args[1]);
                max = Integer.valueOf(args[3]);
                dst = new File(args[2]).getAbsolutePath() + "/";
                traverse(dir);
            } else if (args[0].equals("continue")) {
                bs.file2dict("dict");
                File file = new File(args[1]);
                if (!file.exists())
                    file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write("\r\n\r\n************\r\n\r\n");
                System.out.println("************  12*" + args[3]);
                File root = new File(args[2]);
                File[] files = root.listFiles();
                String clable = args[4];
                int k = 0, len = files.length;
                System.out.println(len);
                for (; k < len; ++k) {
                    if (files[k].getName().equals(args[3])) {
                        System.out.println("get " + files[k].getName());
                        break;
                    }
                }
                System.out.println(k);
                for (; k < len; ++k) {
                    if (isEXE(files[k].getAbsolutePath())) {
                        bw.write(bs.createFeature(files[k].getAbsolutePath()) + clable + "\r\n");
                        System.out.println(clable + "->" + k + "\n" + files[k].getName());
                    }
                }
                bw.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}