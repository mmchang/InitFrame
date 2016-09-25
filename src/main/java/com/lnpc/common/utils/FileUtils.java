package com.lnpc.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 文件操作类
 * 
 * @author changjq
 * 
 */
public class FileUtils {
	private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

	/**
	 * 判断文件是否存在
	 * 
	 * @param file
	 * @return true 存在;false 不存在
	 */
	public static boolean isExistFile(String file) {
		return (new File(file)).exists();
	}

	/**
	 * 获取文件最后保存时间
	 * 
	 * @param file
	 * @return 文件modified
	 */
	public static long getLastModified(String file) {
		return new File(file).lastModified();
	}

	/**
	 * 读取文件
	 * 
	 * @param file
	 * @return 文件内容
	 * @throws Exception
	 */
	public static String readFile(String file) throws Exception {
		File f = new File(file);
		if (f.exists()) {
			FileInputStream fis = new FileInputStream(f);
			int size = fis.available();
			if (size < 1 * 1024 * 1024) {
				logger.info("The files's size is {}",size);
				byte[] buffer = new byte[size];
				int readLength = fis.read(buffer);
				fis.close();
				return new String(buffer, 0, readLength);
			} else {
				fis.close();
				logger.error("The File is too large!");
				return null;
			}
		} else {
			logger.error("The File does not exist!");
			return null;
		}
	}

	/**
	 * 拷贝文件
	 * 
	 * @param is
	 * @param os
	 */
	public static void copyFile(InputStream is, OutputStream os) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(os);
			int len = 0;
			byte[] bytes = new byte[1024 * 2];
			while ((len = bis.read(bytes)) != -1) {
				bos.write(bytes, 0, len);
			}
			bos.flush();
		} catch (FileNotFoundException e) {
			logger.error("The source file does not exists...");
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Error occurs when copy file...");
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					logger.error("Error occurs when close inputstream...");
					e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					logger.error("Error occurs when close outputstream...");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 文件重命名
	 * 
	 * @param srcFile
	 * @param desFile
	 * @return true 成功;false 失败
	 */
	public static boolean renameFile(String srcFile, String desFile) {
		File src = new File(srcFile);
		File des = new File(desFile);
		return src.renameTo(des);
	}

	/**
	 * 拷贝文件
	 * 
	 * @param srcFilePath
	 * @param desFilePath
	 */
	public static void copyFile(String srcFilePath, String desFilePath) {
		File scrFile = new File(srcFilePath);
		if (scrFile.exists()) {
			File desFile = new File(desFilePath);
			if (scrFile.isFile()) {
				try {
					copyFile(new FileInputStream(scrFile), new FileOutputStream(desFile));
				} catch (FileNotFoundException e) {
					logger.error("The source file does not exist...");
					e.printStackTrace();
				}
			} else if (scrFile.isDirectory()) {
				copyDirectiory(srcFilePath, desFilePath);
			}
		} else {
		}
	}

	/**
	 * 拷贝文件夹
	 * 
	 * @param sourceDir
	 * @param targetDir
	 */
	public static void copyDirectiory(String sourceDir, String targetDir) {
		(new File(targetDir)).mkdirs();
		File[] file = (new File(sourceDir)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				File sourceFile = file[i];
				File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
				try {
					copyFile(new FileInputStream(sourceFile), new FileOutputStream(targetFile));
				} catch (FileNotFoundException e) {
					logger.error("The source Directiory does not exist...");
					e.printStackTrace();
				}
			}
			if (file[i].isDirectory()) {
				String dir1 = sourceDir + "/" + file[i].getName();
				String dir2 = targetDir + "/" + file[i].getName();
				copyDirectiory(dir1, dir2);
			}
		}
	}

	/**
	 * 写文件 UTF8编码
	 * 
	 * @param fileName
	 *            文件路径
	 * @param fileContent
	 *            文件内容
	 */
	public static void write(String fileName, String fileContent) {
		write(fileName, fileContent, "UTF-8");
	}

	/**
	 * 写文件
	 * 
	 * @param fileName
	 *            文件路径
	 * @param fileContent
	 *            文件内容
	 * @param encoding
	 *            编码
	 */
	public static void write(String fileName, String fileContent, String encoding) {
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		try {
			fos = new FileOutputStream(fileName);
			osw = new OutputStreamWriter(fos, encoding);
			osw.write(fileContent);
			osw.flush();
		} catch (FileNotFoundException e) {
			logger.error("The file does not exist...");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			logger.error("Error occurs when write file...");
			e1.printStackTrace();
		} catch (IOException e) {
			logger.error("Error occurs when write file...");
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
					fos = null;
				} catch (IOException e) {
					logger.error("Error occurs when close stream...");
					e.printStackTrace();
				}
			}
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					logger.error("Error occurs when close stream...");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 删除单一文件
	 * 
	 * @param filePath
	 */
	public static void deleteSingleFile(String filePath) {
		File f = new File(filePath);
		if (f.exists()) {
			f.delete();
		}
	}

	/**
	 * 删除文件或文件夹
	 * 
	 * @param path
	 */
	public static void deleteFile(String path) {
		File f = new File(path);
		if (!f.exists()) {
			return;
		}
		if (f.isFile()) {
			deleteSingleFile(path);
		} else {
			deleteDirectory(path);
		}
	}

	/**
	 * 删除文件夹
	 * 
	 * @param path
	 */
	private static void deleteDirectory(String path) {
		String dirPath = path;
		if (!dirPath.endsWith(File.separator)) {
			dirPath = dirPath + File.separator;
		}
		File file = new File(dirPath);
		if (!file.isDirectory()) {
			return;
		}
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			deleteFile(files[i].getAbsolutePath());
		}
		file.delete();
	}

	/**
	 * 创建文件夹
	 * 
	 * @param filePath
	 */
	public static void makeDir(String filePath) {
		File f = new File(filePath);
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	/**
	 * 获取文件名
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param filePath
	 *            文件路径
	 * @return 文件名
	 */
	public static String getFileNameByPath(String filePath) {
		String name = "";
		if (filePath != null && !"".equals(filePath)) {
			int pos = filePath.lastIndexOf(File.separator);
			name = filePath.substring(pos + 1);
		}
		return name;
	}

	/**
	 * 序列化到指定文件
	 * 
	 * @author changjq
	 * @date 2014年12月2日
	 * @param filePath
	 *            文件路径
	 * @param obj
	 *            待写入的对象
	 */
	public static void serializeToFile(String filePath, Object obj) {
		if (obj instanceof Serializable) {
			try {
				FileOutputStream fos = new FileOutputStream(filePath);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(obj);
				oos.flush();
				oos.close();
				fos.close();
			} catch (FileNotFoundException e) {
				logger.error("The source file does not exists...");
				e.printStackTrace();
			} catch (IOException e) {
				logger.error("Error occurs when serializeToFile...");
				e.printStackTrace();
			}
		}
	}

	/**
	 * 反序列化
	 * 
	 * @author changjq
	 * @date 2014年12月2日
	 * @param filePath
	 * @return specified对象
	 */
	public static Object deserialize(String filePath) {
		Object retObj = null;
		if(new File(filePath).exists()){
			ObjectInputStream ois = null;
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(filePath);
				ois = new ObjectInputStream(fis);
				retObj = ois.readObject();
			} catch (IOException e) {
				logger.error("Error occurs when deserialize...");
				logger.error("The filepath is:"+filePath);
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				logger.error("Error occurs when readObject...");
				e.printStackTrace();
			} finally {
				try {
					ois.close();
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return retObj;
	}
}
