package utils;

import entity.Error;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FileCopier {
    public static void copyFiles(List<Error> filePaths) throws IOException {
        for (Error error : filePaths) {
            File sourceFile = new File(error.getFilePath());
            String targetFilePath = sourceFile.getParent() + File.separator + sourceFile.getName().substring(0,sourceFile.getName().lastIndexOf("."))+".copy"+sourceFile.getName().substring(sourceFile.getName().lastIndexOf("."));
            error.setFilePath(targetFilePath);
            File targetFile = new File(targetFilePath);
            FileInputStream inputStream = new FileInputStream(sourceFile);
            FileOutputStream outputStream = new FileOutputStream(targetFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
        }
    }
}
