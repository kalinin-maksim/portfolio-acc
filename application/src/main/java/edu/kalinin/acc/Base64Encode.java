package edu.kalinin.acc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;

public class Base64Encode {

    public static final int CHUNK_SIZE = 4000;

    public static void main(String[] args) {
        File inputFile = new File("C:\\Users\\20002178\\caldera.stand\\acc\\application\\src\\main\\java\\ru\\sber\\caldera\\be\\accounting\\2024-04-03.rar");
        File encodedFile = new File("C:\\Users\\20002178\\caldera.stand\\acc\\application\\src\\main\\java\\ru\\sber\\caldera\\be\\accounting\\2024-04-03"+ CHUNK_SIZE +".txt");
        try {
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
            String base64String = Base64.getEncoder().encodeToString(inputBytes);
            FileWriter writer = new FileWriter(encodedFile.getAbsolutePath());
            for (int i = 0; i < base64String.length(); i += CHUNK_SIZE) {
                if (i + CHUNK_SIZE < base64String.length()) {
                    writer.write(base64String.substring(i, i + CHUNK_SIZE) + "\n");
                } else {
                    writer.write(base64String.substring(i) + "\n");
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
