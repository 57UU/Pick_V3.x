package xyz.blockers.pick;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Tools {
    public static void write(String t) throws IOException {
        BufferedWriter writer=new BufferedWriter(new FileWriter("Config.json", StandardCharsets.UTF_8));
        writer.write(t);
        writer.close();
    }
    public static String read(String fileName) throws IOException {
        BufferedReader reader=new BufferedReader(new FileReader(fileName,StandardCharsets.UTF_8));
        StringBuilder sb=new StringBuilder();
        String i;
        while ((i= reader.readLine())!=null){
            sb.append(i);
        }
        return sb.toString();
    }
}
