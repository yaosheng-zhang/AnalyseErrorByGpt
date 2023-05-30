package utils;

import entity.Error;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * 解析Xml结果文件成Errors列表
 */
public class XmlExporter{
    public List<Error> exporter(String path) throws Exception {
        File file = new File(path);
        List<Error> list = new ArrayList<>();
        Document parse = parse(file);
        Element rootElement = parse.getRootElement();
        List<Element> errors = rootElement.element("errors").elements();

        errors.forEach(
                element->{
                    String msg = element.attribute("msg").getValue();
                    Element location = element.element("location");
                    String fileAdr = location.attribute("file").getValue();
                    Integer line = Integer.valueOf(location.attribute("line").getValue());

                    Error error = CppMethodExtractor.extracted(fileAdr, line);
                    error.setMsg(msg);
                    list.add(error);
                }
        );

        try {
            //将所有的xml的数据封装进error中在进行细致处理
            trakleErrors(list);
        }catch (Exception e)
        {
            LogUtils.logError("处理数据时异常"+e);
        }


        return list;
    }


    /**
     * 对Error 中重复方法进行处理
     * @param exporter
     */
    public void trakleErrors (List<Error> exporter)
    {
        HashMap<Integer,Integer> CodeHash= new HashMap<>();

        List<Integer> nums=new ArrayList<>();
        for (int i = 0; i < exporter.size(); i++) {
            String filePath = exporter.get(i).getFilePath();
            Integer beginLine = exporter.get(i).getBeginLine();
            String str =filePath+beginLine;
            int hash = Objects.hash(str);
            if (CodeHash.containsKey(hash))
            {
                Integer index = CodeHash.get(hash);
                Error error = exporter.get(index);
                if (!error.getMsg().contains(exporter.get(i).getMsg()))
                {
                    error.setMsg(error.getMsg() +"&&"+exporter.get(i).getMsg());
                }
                error.setContext(exporter.get(i).getContext());
                nums.add(i);

            }else {
                CodeHash.put(hash,i);
            }
        }
        List<Error> toRemove = new ArrayList<>();
        for (Integer num : nums) {
            toRemove.add(exporter.get(num));
        }
        exporter.removeAll(toRemove);

    }

    public Document parse(File file) throws DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(file);
    }


}
