package com.framework.io;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 支持IO的工具类
 * 
 * @author yanhw
 * 
 */
public class IOUtils {

	/**
	 * 生成字符串文件
	 * 
	 * @param path
	 *            文件路径
	 * @param target
	 *            目标字符串
	 * @return
	 * @throws IOException
	 */
	public static String readStringFromFile(String path) throws IOException {
		File file = new File(path);
		return readStringFromFile(file);
	}

	/**
	 * 生成字符串文件
	 * 
	 * @param path
	 *            文件路径
	 * @param target
	 *            目标字符串
	 * @return
	 * @throws IOException
	 */
	public static String readStringFromFile(File file) throws IOException {
		InputStream input = null;
		String fileSql = null;
		try {
			byte[] fileBytes = new byte[(int) file.length()]; // 文件不可过大
			input = new FileInputStream(file);
			input.read(fileBytes);
			fileSql = new String(fileBytes);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (input != null)
				input.close();
		}
		return fileSql;
	}

	/**
	 * 生成字符串文件
	 * 
	 * @param path
	 *            文件路径
	 * @param target
	 *            目标字符串
	 * @return
	 * @throws IOException
	 */
	public static String writeStringToNewFile(String path, String target) throws IOException {
		File file = new File(path);
		return writeStringToNewFile(file, target);
	}

	public static int SUBDIVSOR = 2000000;

	/**
	 * 生成字符串文件
	 * 
	 * @param path
	 *            文件路径
	 * @param target
	 *            目标字符串
	 * @return
	 * @throws IOException
	 */
	public static String writeStringToNewFile(File file, String target) throws IOException {
		OutputStream out = null;
		try {
			if (file.exists()) {
				if (!file.delete()) {
					throw new IOException("Delete file " + file.getAbsolutePath() + " failed. The file may been used by other programs.");
				}
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			out = new BufferedOutputStream(new FileOutputStream(file));
			if (target.length() > SUBDIVSOR) {
				int count = target.length() / SUBDIVSOR;
				for (int i = 0; i < count; i++) {
					String subString = target.substring(i * SUBDIVSOR, (i + 1) * SUBDIVSOR);
					out.write(subString.getBytes());
					out.flush();
				}
				if(target.length() % SUBDIVSOR > 0){
					String subString = target.substring(count * SUBDIVSOR,target.length());
					out.write(subString.getBytes());
					out.flush();
				}
			} else {
				out.write(target.getBytes());
				out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (out != null)
				out.close();
		}
		String aPath = file.getAbsolutePath();
		return aPath;
	}

	public static Map<String, String> readProperties(String path) throws IOException {
		Map<String, String> map = new LinkedHashMap<String, String>();
		File f = new File(path);
		BufferedReader br = new BufferedReader(new FileReader(f));
		try {
			String s = null;
			do {
				s = br.readLine();
				if (s == null)
					break;
				if (s.indexOf('=') > 0) {
					String[] strs = s.split("=", 2);
					map.put(strs[0].trim(), strs[1].trim());
				}
			} while (true);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (br != null)
				br.close();
		}
		return map;
	}
	
	public static void appendStringToFile(String path,String target)throws IOException {
		appendStringToFile(new File(path), target);
	}
	
	/**
	 * 文件不能超过1-2G
	 * @param file
	 * @param target
	 * @throws IOException 
	 */
	public static void appendStringToFile(File file,String target) throws IOException {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file,"rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw e;
		}
		raf.seek(raf.length());
		try {
			raf.write(target.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}finally{
			if(raf!= null){
				raf.close();
			}
		}
	}
	
	public static void deleteFile(String path)throws IOException{
		File file = new File(path);
		deleteFile(file);
	}
	
	public static void deleteFile(File file)throws IOException{
		if (file.exists()) {
			if (!file.delete()) {
				throw new IOException("Delete file " + file.getAbsolutePath() + " failed. The file may been used by other programs.");
			}
		}
	}

}
