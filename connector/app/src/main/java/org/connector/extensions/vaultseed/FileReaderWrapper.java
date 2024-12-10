package org.wccsconnector.extensions.vaultseed;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileReaderWrapper {

    public static String getFileData(String filepath){
        String data = "";
        try (BufferedReader privateKeyReader = new BufferedReader(new FileReader(filepath))){
            String line;
            while ((line=privateKeyReader.readLine()) != null){
                data += line;
                data += "\n";
            }
        } catch (Exception error){
            System.out.println(error);
        }
        return data;
    }

}