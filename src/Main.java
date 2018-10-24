import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 * 测试
 *
 * @author fangzhiyang
 */
public class Main {

    public static void main(String[] args) throws Exception {
        String model = "data/pe_byte.model";
        String sample = "data/samples/write.exe";
//        String sample = "data/Backdoor.Win32.Hupigon.zay";
        Classifier clss = (Classifier) weka.core.SerializationHelper.read(model);

        ByteGramAndPeStruct byte_pe = new ByteGramAndPeStruct();

        if (!byte_pe.isLoadDict()) {
            byte_pe.file2dict("data/dict");
        }

        if (FileType.isEXE(sample) && byte_pe.createSingleArff(sample)) {
            // malware or benign
            System.out.println(classifyInstance(clss, sample + ".arff"));
        } else {
            System.out.println("ClassifyError");
        }
    }

    /**
     * 根据arff文件判断
     */
    private static String classifyInstance(Classifier mClassifier, String arff) throws Exception {
        Instances test = ConverterUtils.DataSource.read(arff);
        test.setClassIndex(test.numAttributes() - 1);
        double pred = mClassifier.classifyInstance(test.instance(0));

        return test.classAttribute().value((int) pred);
    }
}
