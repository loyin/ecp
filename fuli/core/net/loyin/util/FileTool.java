package net.loyin.util;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

public class FileTool {
	public long folderCount;
	public long flieCount;
	public long totalSize;
	public static FileTool me = new FileTool();

	/**
	 * 取文件大小
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public long getFileSize(File f) {// 取得文件大小
		try {
			if (f.exists()) {
				FileInputStream fis = new FileInputStream(f);
				return fis.available();
			}
		} catch (Exception e) {
		}
		return 0;
	}

	/**
	 * 文件夹大小
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	// 递归
	public void getFolderSize(File f) throws Exception// 取得文件夹大小
	{
		File flist[] = f.listFiles();
		for (File fc : flist) {
			if (fc.isDirectory()) {
				folderCount++;
				getFolderSize(fc);
			} else {
				flieCount++;
				totalSize += fc.length();
			}
		}
	}

	public String FormetFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#0.###");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "GB";
		}
		return fileSizeString;
	}

	public static void main(String args[]) {
		FileTool g = new FileTool();
		long startTime = System.currentTimeMillis();
		try {
			String path = "E:\\temp";
			File ff = new File(path);
			g.getFolderSize(ff);
			System.out.println(g.FormetFileSize(g.totalSize));
			System.out.println(g.flieCount);
			System.out.println(g.folderCount);

		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("总共花费时间为：" + (endTime - startTime) + "毫秒...");
	}
}
