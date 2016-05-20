package com.whw.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by wuhaiwen on 2016/4/20.
 */
public class FileUtil {

    public static int index = 0;

    Integer a = Integer.valueOf(127);

    /**
     * 统计文件夹个数
     * @param file 源文件
     * @return
     */
    public static int countDir(File file) {

        int count = 0;
        File file1[] = file.listFiles();
        for (File f : file1) {
            if (f.isDirectory()) {
                count++;
            }
        }
        return count;
    }

    /**
     * 统计文件个数
     * @param file
     * @return
     */
    public static int countFile(File file) {
        int count = 0;
        File file1[] = file.listFiles();
        for (File f : file1) {
            if (f.isFile()) {
                count++;
            }
        }
        return count;
    }


    /**
     * 给文件重命名
     * @param file
     * @param newName
     */
    public static File rename(File file,String newName){

        String pathName = file.getParentFile().getPath()+"/"+newName;
        File newFile = new File(pathName);
        file.renameTo(newFile);
        return  newFile;
    }

    /**
     * 复制文件夹
     * @param src 要复制的原文件
     * @param dest  复制文件的目的文件
     * @throws IOException
     */
    public static void copyFolder(File src,File dest) throws IOException{
        dest.mkdir();
        File[] files = src.listFiles();
        for(File file:files){
            File sDest = new File(dest.getPath()+"/"+file.getName());
            if(file.isDirectory()){
                copyFolder(file, sDest);
            }else if(file.isFile()){
                copyFile(file,sDest);
            }
        }

    }

    /**
     * 复制文件
     * @param src
     * @param sDest
     * @throws IOException
     */
    public static void copyFile(File src,File sDest) throws IOException{
        FileInputStream inputStream = new FileInputStream(src);
        FileOutputStream outputStream = new FileOutputStream(sDest);
        byte[] buf = new byte[1024 * 10];
        int size;
        while (-1 != (size = inputStream.read(buf))) {
            outputStream.write(buf, 0, size);
        }
        inputStream.close();
        outputStream.close();

    }


    /**
     * 创建文件夹
     * @param file
     * @param name
     * @param data
     * @return
     */
    public static boolean createFolder(File file,String name,List<File> data){
        File f = new File(file.getAbsolutePath()+"/"+name);
        //首先判断在该目录下有没有同名的文件，如果有，则在新建的文件夹后加标记序号
        if(data.contains(f)){
            f = new  File(file.getAbsolutePath()+"/"+name+"("+(index++)+")");
        }
        data.add(f);
        return f.mkdirs();
    }



    /**
     * 删除文件夹
     * @param src
     */
    public static  void deleteFolders(File src){
        File[] files = src.listFiles();
        if(files.length>0){
            for(File f:files){
                if(f.isDirectory()){
                    //判断是否为文件夹，如果是，则递归调用该方法
                    deleteFolders(f);
                }
                if(f.isFile()){
                    f.delete();
                }
            }
        }else {
            src.delete();
        }
    }


    /**
     * 文件大小单位
     * @param size  源文件的长度
     * @return
     */
    public static String getSize(long size) {
        if (size < 1024) {
            return String.format("%.1f",(double)size) + "bytes";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f",size/(double)1024) + "Kb";
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f",size/1024/(double)1024) + "MB";
        } else {
            return "unkown size";
        }
    }
}
