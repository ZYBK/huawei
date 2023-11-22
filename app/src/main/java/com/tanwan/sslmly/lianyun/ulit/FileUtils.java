package com.tanwan.sslmly.lianyun.ulit;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class FileUtils {

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                throw new RuntimeException("IOException occurred. ", e);
            }
        }
    }

    public static boolean isEmpty(CharSequence str) {
        return (str == null || str.length() == 0);
    }

    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }

    public static boolean isFileExist(String filePath) {
        if (isBlank(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    public static boolean writeFile(String filePath, InputStream stream) {
        return writeFile(filePath, stream, false);
    }

    public static boolean writeFile(String filePath, InputStream stream, boolean append) {
        return writeFile(filePath != null ? new File(filePath) : null, stream, append);
    }

    public static boolean writeFile(File file, InputStream stream, boolean append) {
        OutputStream o = null;
        try {
            makeDirs(file.getAbsolutePath());
            o = new FileOutputStream(file, append);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = stream.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            close(o);
            close(stream);
        }
    }

    public static boolean writeFile(File file, byte data[]){
        OutputStream o = null;
        try {
            makeDirs(file.getAbsolutePath());
            o = new FileOutputStream(file, false);
            o.write(data, 0, data.length);
            o.flush();
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            close(o);
        }
    }

    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (isEmpty(folderName)) {
            return false;
        }
        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }
    public static String getFolderName(String filePath) {
        if (isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        filePath = (filePosi == -1) ? filePath : filePath.substring(0, filePosi+1);

        int filePosi2 = filePath.lastIndexOf("/");
        filePath = (filePosi2 == -1) ? filePath : filePath.substring(0, filePosi2+1);
        return filePath;
    }

    public static boolean deleteFile(String path) {
        if (isBlank(path)) {
            return true;
        }
        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f.getAbsolutePath());
            }
        }
        return file.delete();
    }
    public static int getLength(String path) {
        File f= new File(path);
        if (f.exists() && f.isFile()){
            return (int) f.length();
        }
        return 0;
    }

    /**
     * 加载字节文件
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static byte[] fileLoadByte(String path) throws IOException {
        InputStream fis=null;
        try {
            fis = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
//            log.error("", e);
        }

        if (fis == null) {
            throw new FileNotFoundException("file not exists");
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream(fis.available());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(fis);
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
//            log.error("", e);
            throw e;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                in.close();
            } catch (IOException e) {
//                log.error("", e);
                e.printStackTrace();
            }
            bos.close();
        }
    }
}
