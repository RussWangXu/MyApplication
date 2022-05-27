package com.example.myapplication.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;


import com.example.myapplication.R;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;


public class FileUtil {

    public static String createDir(String path) {
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs(); // 创建父文件夹路径
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 保存一个暂时的bitmap图片
     * <p>
     * 保存目录在
     *
     * @param b
     */
//    public static String saveTmpBitmap(Bitmap b, String name) {
//        String result = "";
//        String jpegName = MyConfig.ROOT_CACHE + File.separator + MyConfig.FACE_DIR + File.separator + name + ".jpg";
//        Log.d("FileUtil", jpegName);
//        result = jpegName;
//        try {
//            FileOutputStream fout = new FileOutputStream(jpegName);
//            BufferedOutputStream bos = new BufferedOutputStream(fout);
//            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//            bos.flush();
//            bos.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return result;
//    }

    /**
     * 旋转图片
     *
     * @param angle  被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    public static Bitmap rotaingBitmap(int angle, Bitmap bitmap) {
        //bitmap = small(bitmap);   不缩放
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        return returnBm;
    }

    /**
     * 判断文件夹是否存在，如果不存在，就新建一个
     *
     * @param mDirPath
     */
    public static void decideDirExist(String mDirPath) {
        File file = new File(mDirPath);
        //如果文件夹不存在则创建
        if (!file.exists() && !file.isDirectory()) {
            file.mkdir();
        }
    }


    /**
     * 将Bitmap转换成文件
     * 保存文件
     *
     * @param bm
     * @param filename
     * @throws IOException
     */
    public static File saveFile(Bitmap bm, String filename, String filepath) throws IOException {
        File file = new File(filepath + filename);
        if (file.exists()) {
            file.delete();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
        return file;
    }

    /**
     * 将文件保存到内部存储里面
     */
    public static void writeFile(byte[] content, String path) {
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bw = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
            bw.write(String.valueOf(content));
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fos != null)
                    fos.close();
            } catch (IOException ie) {
            }
        }
    }

    /**
     * 将文件保存到内部存储里面
     */
    public static void writeFile(String content, String path) {
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bw = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
            bw.write(content);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fos != null)
                    fos.close();
            } catch (IOException ie) {
            }
        }
    }

    /**
     * 将文件保存到内部存储里面  富态内容
     */
    public static void appendWriteFile(String content, String path) {
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
                fos = new FileOutputStream(file, true);
                bw = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
                bw.write(content);
            } else {
                fos = new FileOutputStream(file, true);
                bw = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
                bw.write("," + content);
            }

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fos != null)
                    fos.close();
            } catch (IOException ie) {
            }
        }
    }

    /**
     * 读取指定路径指定文件
     */
    public static String loadFromSDFile(String pathUrl, String fname) {
        String result = null;
        try {
            File f = new File(pathUrl + fname);
            int length = (int) f.length();
            byte[] buff = new byte[length];
            FileInputStream fin = new FileInputStream(f);
            fin.read(buff);
            fin.close();
            result = new String(buff, "UTF-8");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            if (fname.contains("login")) {
//                writeFile("1", DataComfig.SAFETY_LOGIN_PATH + "loginErrorNumbers.txt");
//                loadFromSDFile(DataComfig.SAFETY_LOGIN_PATH, DataComfig.LOGIN_ERROR_NUMBERS);
                return "1";
            } else {
                //读取数据失败返回
                return "load_data_error";
            }
        }
    }

    /**
     * 读取指定路径指定文件
     */
    public static String loadFromSDFile(String pathUrl) {

        if (!decideFileExist(pathUrl)) {
            return null;
        }
        String result = null;
        try {
            File f = new File(pathUrl);
            int length = (int) f.length();
            byte[] buff = new byte[length];
            FileInputStream fin = new FileInputStream(f);
            fin.read(buff);
            fin.close();
            result = new String(buff, "UTF-8");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean decideFileExist(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return true;
        }
        return false;
    }

    /**
     * 删除指定文件夹下全部文件
     *
     * @param deleteUrl
     */
    public static void deleteSDFile(String deleteUrl) {
        File file = new File(deleteUrl);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
//                    deleteSDFile(DataComfig.SD_PATH_EXTERNAL + "ziyuan/jie/" + files[i].getName());
                } else {
                    files[i].delete();
                }
            }
//            if(!file.getName().equals("jie")){
//            file.delete();
//            }
        }
    }

    /**
     * 是否包含文件夹
     *
     * @param url
     */
    public static boolean fileIsExists(String url) {
        try {
            File file = new File(url);
            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * @param
     */
    public static void saveImage(Bitmap bmp) {
//        File appDir = new File(DataComfig.SD_PATH_EXTERNAL.substring(0, DataComfig.SD_PATH.length() - 1), "eBook/home/");
//        if (!appDir.exists()) {
//            appDir.mkdir();
//        }
//        String fileName = "history_home.jpg";
//        File file = new File(appDir, fileName);
//        try {
//            FileOutputStream fos = new FileOutputStream(file);
//            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos.flush();
//            fos.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static int getImage(String name) {
        Class drawable = R.drawable.class;
        Field field = null;
        try {
            field = drawable.getField(name);
            int images = field.getInt(field.getName());
            return images;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
