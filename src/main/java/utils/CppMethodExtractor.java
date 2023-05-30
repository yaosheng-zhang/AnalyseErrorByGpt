package utils;

import entity.Error;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 通过指定行提取上下文函数
 */
public class CppMethodExtractor {
    public static Error extracted(String filePath, Integer lineNumber) {
        Error error = new Error();
        error.setFilePath(filePath);
        error.setLine(lineNumber);
        StringBuilder context=new StringBuilder();
        List<String> lines = new ArrayList<>();
        try {
            lines = readCppFile(filePath);
            List<String> methodLines = extractMethod( error ,lines, lineNumber);
            if (!methodLines.isEmpty()) {
                for (String methodLine : methodLines) {
                    context.append(methodLine+"\n");
                }
                error.setContext(context.toString());
            }
            else {

                error.setContext(lines.get(lineNumber-1));
            }



        } catch (IOException e) {
            System.err.println("读取文件时出错: " + e.getMessage());
        }
        return error;
    }

    private static List<String> readCppFile(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    private static List<String> extractMethod(Error error,List<String> lines, int lineNumber) {
        // Initialize variables to store the start and end of the method
        int methodStart = -1;
        int methodEnd = -1;
        // Initialize the number of braces to 0
        int braceCount = 0;
        // Create a pattern to find the start of a method
        Pattern methodStartPattern = Pattern.compile("\\w+\\s+\\w+\\s*\\(.*\\)\\s*(const)?\\s*\\{|\\w+\\s+\\w+\\s*\\(.*\\)");
        Pattern structStartPattern = Pattern.compile("typedef\\s+(struct|union)\\s+\\w+\\s*\\{|typedef\\s+(struct|union)\\s+\\w+\\s*|typedef\\s+(struct|union)\\s+\\{|typedef\\s+(struct|union)\\s+|(struct|union)\\s+\\w+\\s*\\{|(struct|union)\\s+\\w+\\s*|(struct|union)\\s+\\{|(struct|union)\\s+");
        //判断是否是函数头
        String s = lines.get(lineNumber - 1);
        Matcher matcher1 = methodStartPattern.matcher(s);
        Matcher matcher2 = structStartPattern.matcher(s);
        if (matcher1.find()||matcher2.find())
        {
            methodStart=lineNumber-1;
            methodEnd=findMethodEnd(lines,methodStart,0);
        }

        else {
            // Iterate through the lines
            for (int i = 0; i < lines.size(); i++) {
                // Get the current line
                String line = lines.get(i);
                // If the line is not the line number minus 1
                if (i < lineNumber - 1) {
                    // Find the start of the method
                    Matcher matcher = methodStartPattern.matcher(line);
                    Matcher matcher3=structStartPattern.matcher(line);
                    // If the method is found
                    if (matcher.find()||matcher3.find()) {
                        // Set the method start to the current line
                        methodStart = i;
                        // Set the number of braces to 1
                        braceCount +=countBraces(line);
                    } else if (methodStart >= 0) {
                        // Increment the number of braces
                        braceCount += countBraces(line);
                        // If the number of braces is 0, set the method start to -1
                        if (braceCount == 0) {
                            methodStart = -1;
                        }
                    }
                } else if (i == lineNumber - 1) {
                    // If the method is found
                    if (methodStart >= 0) {
                        // Find the end of the method
                        methodEnd = findMethodEnd(lines, i, braceCount);
                        // Break out of the loop
                        break;
                    }
                }
            }

        }

        // If the method start and end are found
        if (methodStart >= 0 && methodEnd >= 0) {
            error.setBeginLine(methodStart+1);
            error.setEndLine(methodEnd+1);
            return lines.subList(methodStart, methodEnd + 1);
        } else {
            // Otherwise, return an empty list
            error.setBeginLine(lineNumber);
            error.setEndLine(lineNumber);
            return new ArrayList<>();
        }
    }

    private static int countBraces(String line) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (c == '{') {
                count++;
            } else if (c == '}') {
                count--;
            }
        }
        return count;
    }

    private static int findMethodEnd(List<String> lines, int startLine, int braceCount) {

        for (int i = startLine; i < lines.size(); i++) {
            String line = lines.get(i);
            if (countBraces(line)==0)
            {
                continue;
            }
            braceCount += countBraces(line);
            if (braceCount == 0) {
                return i;
            }
        }
        return -1;
    }
}