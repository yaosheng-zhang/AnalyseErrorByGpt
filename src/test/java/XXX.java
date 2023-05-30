
import chatGPT.Generator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Error;
import org.junit.jupiter.api.Test;
import chatGPT.CodeGetter;

import utils.CppMethodExtractor;
import utils.FileCopier;
import utils.LogUtils;
import utils.XmlExporter;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class XXX {
    @Test
    public void testGPT(){

      String code="typedef struct record_t \n" +
              "{\n" +
              " uint16_t key;\n" +
              " uint16_t val;\n" +
              "} record1_t;\n";
//        String message = " 你的任务是将下面的代码根据MISRA的要求进行重构\n 代码以```包裹 违反的MISRA规则以<>包裹 \n```"++"```\n"+"<"+errors.get(i).getMsg()+">\n返回的结果中只允许出现重构后的代码，非代码部分必须以JAVA注释 // 的方式出现";
    }
    @Test
    void testXml() throws Exception {
        String path="D:\\project\\chapGPT\\src\\test\\files\\cppcheck-result.xml";
        XmlExporter xmlExporter = new XmlExporter();
        List<Error> exporter = xmlExporter.exporter(path);
        System.out.println(exporter);
    }


    @Test
    void testSimilarFunc() throws Exception {
        String path="D:\\project\\chapGPT\\src\\test\\files\\testParase.xml";
        HashMap<String,Integer> CodeHash= new HashMap<>();
        XmlExporter xmlExporter = new XmlExporter();
        List<Error> exporter = xmlExporter.exporter(path);
        List<Integer> nums=new ArrayList<>();
        for (int i = 0; i < exporter.size(); i++) {
            String filePath = exporter.get(i).getFilePath();
            Integer beginLine = exporter.get(i).getBeginLine();
            String str = filePath + beginLine;
            int hash = Objects.hash(str);
            if (CodeHash.containsKey(hash)) {
                Integer index = CodeHash.get(hash);
                Error error = exporter.get(index);
                if (!error.getMsg().contains(exporter.get(i).getMsg())) {
                    error.setMsg(error.getMsg() + "&&" + exporter.get(i).getMsg());
                }
                nums.add(i);
            }
        }
        List<Error> toRemove = new ArrayList<>();
        for (Integer num : nums) {
            toRemove.add(exporter.get(num));
        }
        exporter.removeAll(toRemove);
        for (Error error : exporter) {
            System.out.println(error);
        }

    }
    

    @Test
    void testCodeGetter(){
        String path="D:\\project\\chapGPT\\src\\test\\files\\testParase.xml";
        CodeGetter.settleSmell(path);
    }


    @Test
    void testFuncExtraction(){
        String path ="D:\\project\\chapGPT\\src\\test\\files\\main.cpp";
        Error extracted = CppMethodExtractor.extracted(path, 3);
        System.out.println(extracted);
//        System.out.println(extracted);


    }
    @Test
    void extract() throws IOException {
        File file = new File("C:\\Users\\zyc\\Desktop\\工作\\测试数据\\AutoRefactoring.json");
        String jsonStr="";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder sb =new StringBuilder();
            String line="";
            while ((line=bufferedReader.readLine())!=null){
                sb.append(line);
            }
            bufferedReader.close();
            jsonStr=sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(jsonStr);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonStr);
        for (JsonNode node : jsonNode) {

            if (node.get("Example")!=null)
            {
                String category = node.get("Category").asText();
                String replace = category.replace(" ", "_")+".c";
                String example = node.get("Example").asText();
                String path = "C:\\Users\\zyc\\Desktop\\工作\\测试数据\\"+replace;
                File file1 = new File(path);
                PrintStream printStream = new PrintStream(file1);
                printStream.print(example);
            }
        }


    }


}
