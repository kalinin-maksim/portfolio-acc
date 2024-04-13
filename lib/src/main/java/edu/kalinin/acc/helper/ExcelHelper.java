package edu.kalinin.acc.helper;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.Logger;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;

import java.io.*;
import java.util.Map;

@UtilityClass
public class ExcelHelper {

    public static byte[] getExcelFileAsBytes(String pathTemplateName, Map<String, Object> data, Logger log) {

        File outputName = null;
        try {
            outputName = File.createTempFile("excel", ".xls");
        } catch (IOException e) {
            log.warn("");
        }
        OutputStream outStream = null;
        try {
            outStream = new FileOutputStream(outputName);
        } catch (FileNotFoundException e) {
            log.warn("");
        }

        fillTemplate(data, outStream, pathTemplateName, log);

        try {
            return new FileInputStream(outputName).readAllBytes();
        } catch (IOException e) {
            log.warn("");
        }

        throw new RuntimeException("");
    }

    public static void fillTemplate(Map<String, Object> data, OutputStream outStream, String pathTemplateName, Logger log) {
        try (InputStream input = ExcelHelper.class.getResourceAsStream(pathTemplateName)) {
            Context context = new Context();
            for (Map.Entry<String, Object> element : data.entrySet()) {
                context.putVar(element.getKey(), element.getValue());
            }
            JxlsHelper.getInstance().processTemplate(input, outStream, context);
        } catch (Exception e) {
            log.warn("Fail to generate the document", e);
        } finally {
            closeAndFlushOutput(outStream, log);
        }
    }

    public static void closeAndFlushOutput(OutputStream outStream, Logger log) {
        try {
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            log.warn("Fail to flush and close the output", e);
        }
    }
}
