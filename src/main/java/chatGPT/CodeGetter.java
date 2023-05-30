package chatGPT;

import entity.Error;
import utils.FileCopier;
import utils.LogUtils;
import utils.XmlExporter;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CodeGetter  {

    public static void main(String[] args) {
        String xmlPath=args[0];

        settleSmell(xmlPath);
    }




    public static void settleSmell(String xmlPath){
        //1.将report信息解析出来
        XmlExporter exporter = new XmlExporter();
        List<Error> errors ;
        try {
          errors = exporter.exporter(xmlPath);
            FileCopier.copyFiles(errors);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        //2.通过模型得到重构的回答
        Generator generator = new Generator();
        for (int i = 0; i < errors.size(); i++) {
            String message = " 你的任务是将下面的代码根据MISRA的要求进行重构\n 代码以```包裹 违反的MISRA规则以<>包裹 \n```"+errors.get(i).getContext()+"```\n"+"<"+errors.get(i).getMsg()+">\n返回的结果中只允许出现重构后的代码，非代码部分必须以JAVA注释 // 的方式出现";
            LogUtils.logDebug(message);
            String codeFromModle = generator.getCodeFromModle(message);
            //处理下因同文件导致的行号错位
            trakleLine(errors, i, codeFromModle);
            LogUtils.logDebug(codeFromModle);
            importFile(errors.get(i),codeFromModle);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }



    }

    private static void trakleLine(List<Error> errors, int i, String codeFromModle) {
        int lineCountDifference = getLineCountDifference(codeFromModle, errors.get(i).getContext());
        for (int j = 0; j < errors.size(); j++) {
            if (j!=i)
            {
                if (errors.get(j).getFilePath().equals(errors.get(i).getFilePath())&&errors.get(j).getBeginLine()>errors.get(i).getBeginLine())
                {
                    errors.get(j).setBeginLine(errors.get(j).getBeginLine()+lineCountDifference);
                    errors.get(j).setEndLine(errors.get(j).getEndLine()+lineCountDifference);
                    errors.get(j).setLine(errors.get(j).getLine()+lineCountDifference);
                }
            }

        }
    }

    public static void importFile(Error error, String code) {

        String content;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(error.getFilePath()));
            String line = bufferedReader.readLine();
            int lineNum=1;
           StringBuilder stringBuilder=new StringBuilder();
            while(line!=null)
            {
                if (lineNum>= error.getBeginLine() && lineNum<= error.getEndLine())
                {
                    if (lineNum==  error.getEndLine())
                    {
                        stringBuilder.append(code+"\n");
                    }
                    lineNum++;
                    line=bufferedReader.readLine();


                }else {
                    stringBuilder.append(line+"\n");
                    lineNum++;
                    line=bufferedReader.readLine();
                }


            }
             content = stringBuilder.toString();
            bufferedReader.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            PrintStream printStream = new PrintStream(error.getFilePath());
            printStream.print(content);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }


    public static String getCode(String classPath,int startLine,int endLine)
    {

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(classPath));
            StringBuilder stringBuilder=new StringBuilder();
            int lineNum=1;
            String line = bufferedReader.readLine();
            while (line!=null){
                if (lineNum>=startLine && lineNum<=endLine)
                {
                    stringBuilder.append(line);
                    stringBuilder.append("\n");
                }
                lineNum++;
                line=bufferedReader.readLine();
            }
            bufferedReader.close();
            return stringBuilder.toString();





        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public static int getLineCountDifference(String code1, String code2) {
        String[] lines1 = code1.split("\n");
        String[] lines2 = code2.split("\n");
        return Math.abs(lines1.length - lines2.length);
    }


    


}
