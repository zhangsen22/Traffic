package com.sample.icontest;

/**
 * @author LiaoLiang
 * @date : 2020/12/2 17:21
 */
public class StringUtil {
    //定义GB的计算常量
    public static String getBytesString(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < (1048576)) {
            float kb = bytes/1024f;
            return kb + " KB";
        }
        float mb = bytes / 1048576f;
        return mb + " MB";
    }

    //将字节数转化为MB
    public static String byteToMB(long size){
        long kb = 1024;
        long mb = kb*1024;
        long gb = mb*1024;
        if (size >= gb){
            float floatGB = (float)size/gb;
           float gbBytes = Float.parseFloat(String.format("%.1f",floatGB)) - Float.parseFloat(String.format("%.0f",floatGB));
            String str = gbBytes == 0?"%.0f GB":"%.1f GB";
            return String.format(str,(float)size/gb);
//            return String.format("%.1f GB",(float)size/gb);
        }else if (size >= mb){
            float fMb = (float) size/mb;
            float mbBytes = Float.parseFloat(String.format("%.1f",fMb)) - Float.parseFloat(String.format("%.0f",fMb));
            String strMB = mbBytes == 0?"%.0f MB":"%.1f MB";
            return String.format(strMB,fMb);
        }else if (size > kb){
            float fkb = (float) size / kb;
            float kbBytes = Float.parseFloat(String.format("%.1f",fkb)) - Float.parseFloat(String.format("%.0f",fkb));
            String strKB = kbBytes == 0?"%.0f kb":"%.1f kb";
            return String.format(strKB,fkb);
        }else {
            return String.format("%d B",size);
        }
    }

    //将字节数转化为MB
    public static String byte2MB(long size){
        long kb = 1024;
        long mb = kb*1024;
        long gb = mb*1024;
        if (size >= gb){
            return String.format("%.1f GB",(float)size/gb);
        }else if (size >= mb){
            float f = (float) size/mb;
            return String.format(f > 100 ?"%.0f MB":"%.1f MB",f);
        }else if (size > kb){
            float f = (float) size / kb;
            return String.format(f>100?"%.0f KB":"%.1f KB",f);
        }else {
            return String.format("%d B",size);
        }
    }

    //将字节数转化为MB
    public static String byte2GB(long size){
        long kb = 1024;
        long mb = kb*1024;
        long gb = mb*1024;
//        if (size >= gb){
        float fMb = (float) size/mb;
        float mbBytes = Float.parseFloat(String.format("%.1f",fMb)) - Float.parseFloat(String.format("%.0f",fMb));
        String strMB = mbBytes == 0?"%.0f MB":"%.1f MB";
        return String.format(strMB,fMb);
//            return String.format("%.1f GB",(float)size/gb);
//        }else if (size >= mb){
//            float fMb = (float) size/mb;
//            float mbBytes = Float.parseFloat(String.format("%.1f",fMb)) - Float.parseFloat(String.format("%.0f",fMb));
//            String strMB = mbBytes == 0?"%.0f MB":"%.1f MB";
//            return String.format(strMB,fMb);
//        }else if (size > kb){
//            float fkb = (float) size / kb;
//            float kbBytes = Float.parseFloat(String.format("%.1f",fkb)) - Float.parseFloat(String.format("%.0f",fkb));
//            String strKB = kbBytes == 0?"%.0f MB":"%.1f MB";
//            return String.format(strKB,fkb);
//        }else {
//            return String.format("%d B",size);
//        }
    }
}
