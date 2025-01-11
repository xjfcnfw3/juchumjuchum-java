package com.ant.juchumjuchum.utils;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class FileUtil {

    public static void unzipFile(java.io.File file) {
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                int length;
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream("./" + zipEntry.getName()));
                while ((length = zipInputStream.read()) != -1) {
                    out.write(length);
                }
                zipInputStream.closeEntry();
                out.close();
            }
            zipInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
