package edu.kalinin.acc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Base64;


public class Base64Decode {

    public static void main(String[] args) {

        File encodedFile = new File("C:\\Users\\20002178\\caldera.stand\\edu-kalinin-acc-release\\out\\unnamed.txt");
        File decodedFile = new File("C:\\Users\\20002178\\caldera.stand\\edu-kalinin-acc-release\\out\\unnamed.zip");
        try {
            FileInputStream fileInputStream = new FileInputStream(encodedFile);
            byte[] encodedBytes = new byte[(int) encodedFile.length()];
            fileInputStream.read(encodedBytes);
            byte[] decodedBytes = Base64.getDecoder().decode(new String(encodedBytes).replace("\n", "").getBytes());
            FileOutputStream fileOutputStream = new FileOutputStream(decodedFile);
            fileOutputStream.write(decodedBytes);
            fileInputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}