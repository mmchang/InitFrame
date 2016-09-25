package com.lnpc.common.utils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 字符串工具类
 * 
 * @author changjq
 * 
 */
public class StringUtils {
	private static Logger logger = LoggerFactory.getLogger(StringUtils.class);
	private static final int[] CARD_XS = new int[] { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7,9, 10, 5, 8, 4, 2 };
	private static final char[] CARD_LAST = new char[] { '1', '0', 'X', '9', '8', '7', '6','5', '4', '3', '2' };

	/**
	 * 将目标字符串srcString的字符数组sign替换为dest
	 * 
	 * @param srcString
	 * @param sign
	 * @param dest
	 * @return 替换后的字符串
	 */
	public static String replace(String srcString, char sign[], char dest) {
		int sLength = sign.length;
		for (int i = 0; i < sLength; i++) {
			srcString = srcString.replace(sign[i], dest);
		}
		return srcString;
	}

	/**
	 * 将目标字符串srcString的字符数组sign替换为空字符
	 * 
	 * @param srcString
	 * @param sign
	 * @return 替换后的字符串
	 */
	public static String replace(String srcString, char sign[]) {
		return replace(srcString, sign, ' ');
	}

	/**
	 * 删除扩展名
	 * 
	 * @param qualifiedName
	 * @return 文件名（无扩展名）
	 */
	public static final String trimExtendName(String qualifiedName) {
		int pos = qualifiedName.lastIndexOf(".");
		if (pos > 0)
			return qualifiedName.substring(0, pos);
		else
			return qualifiedName;
	}

	/**
	 * 获取扩展名
	 * 
	 * @param qualifiedName
	 * @return 扩展名
	 */
	public static final String getExtendName(String qualifiedName) {
		int pos = qualifiedName.lastIndexOf(".");
		if (pos > 0)
			return qualifiedName.substring(pos + 1);
		else
			return "";
	}

	/**
	 * 判断字符串为null或空字符串
	 * 
	 * @param value
	 * @return true 为null或空字符串;false 不为null并且不为空字符串
	 */
	public static boolean checkNullOrEmptyString(String value) {
		if (value == null || "".equals(value))
			return true;
		return false;
	}

	/**
	 * 如果value为null或空字符串，则返回defaultValue<br>
	 * 否则返回value本身
	 * 
	 * @param value
	 * @param defaultValue
	 * @return 指定字符串
	 */
	public static String defaultStringValue(String value, String defaultValue) {
		if (value == null || "".equals(value)) {
			return defaultValue;
		} else {
			return value;
		}
	}

	/**
	 * 如果value为null或空字符串，则返回defaultValue<br>
	 * 否则返回Integer.parseInt(value)
	 * 
	 * @param value
	 * @param defaultValue
	 * @return 指定参数的整数形式
	 */
	public static int defaultIntValue(String value, int defaultValue) {
		if (value == null || "".equals(value)) {
			return defaultValue;
		} else {
			return Integer.parseInt(value);
		}
	}

	/**
	 * 如果value为null或空字符串，则返回defaultValue<br>
	 * 否则返回Float.parseInt(value)
	 * 
	 * @param value
	 * @param defaultValue
	 * @return 指定参数的浮点数形式
	 */
	public static float defaultFloatValue(String value, float defaultValue) {
		if (value == null || "".equals(value)) {
			return defaultValue;
		} else {
			return Float.parseFloat(value);
		}
	}

	/**
	 * 如果value为null或空字符串，则返回defaultValue<br>
	 * 否则返回Double.parseInt(value)
	 * 
	 * @param value
	 * @param defaultValue
	 * @return 指定参数的双精度数形式
	 */
	public static double defaultDoubleValue(String value, double defaultValue) {
		if (value == null || "".equals(value)) {
			return defaultValue;
		} else {
			return Double.parseDouble(value);
		}
	}

	/**
	 * 获取当前时间戳的字符串形式
	 * 
	 * @author changjq
	 * @return 当前时间戳的字符串形式
	 */
	public static String getCurrentTimeMillis() {
		return String.valueOf(System.currentTimeMillis());
	}
	
	/**
	 * 将数据库字段类型编码转为可读字符串形式
	 * @author changjq
	 * @date 2015年6月5日
	 * @param type
	 * @return 指定参数的字符串形式
	 */
	public static String getStringFieldType(int type) {
		switch (type) {
		case -6:
			return "tinyint";
		case 1:
			return "char";
		case 2:
			return "numeric";
		case 4:
			return "integer";
		case 5:
			return "smallint";
		case 6:
			return "float";
		case 7:
			return "real";
		case 8:
			return "double";
		case 12:
			return "varchar";
		case 91:
			return "date";
		case 93:
			return "timestamp";
		case 2004:
			return "blob";
		case 2005:
			return "clob";
		default:
			return null;
		}
	}

	/**
	 * 将由分隔符分隔的字符替换为sign+字符+sign形式<br>
	 * 如：调用signFormat("1:2:3",":","'")后，返回结果为"'1','2','3'"
	 * @author changjq
	 * @date 2014年12月16日
	 * @param str
	 * @param separator
	 * @param sign
	 * @return 替换后的字符串
	 */
	public static String signFormat(String str, String separator, String sign) {
		StringBuffer ret = new StringBuffer(512);
		if (str == null || sign == null || separator == null) {
			return null;
		}
		String[] arr = str.split(separator);
		for (int i = 0; i < arr.length; i++) {
			ret.append(sign);
			ret.append(arr[i]);
			ret.append(sign);
			if (i != arr.length - 1) {
				ret.append(separator);
			}
		}
		return ret.toString();
	}
	public static String getRandomFileName(String fileName){
		Random rd = new Random();
		int num = rd.nextInt(999999)+1;
		String numStr = String.valueOf(num);
		String retName = getCurrentTimeMillis()+"_";
		retName = retName + numStr;
		retName = retName +".";
		retName = retName + getExtendName(fileName);
		return retName;
	}
	/**
	 * 
	 * @Title: isValidCard
	 * @Description: TODO(根据校验码验证身份证号:18--未经过测试)
	 * @author: cjq  
	 * @date:2015年8月17日 上午10:22:08 
	 * @param card
	 * @return
	 * @return: boolean
	 */
	public static boolean isValidCard(String card){
		boolean valid = false;
		if(card!=null && card.length()==18){
			char[] cardChar = card.toCharArray();
			int sum = 0;
			for(int i=0;i<card.length()-1;i++){
				sum += (cardChar[i] - '0') * CARD_XS[i];
			}
			valid =  CARD_LAST[sum % 11] == cardChar[17];
		}
		return valid;
	}
	public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
	public static int arrIndexOf(String[] arrStr,String str){
		int ret = -1;
		if(arrStr!=null && arrStr.length>0){
			for(int i = 0 ; i<arrStr.length;i++){
				if(arrStr[i].equals(str)){
					ret = i;
					break;
				}
			}
		}
		return ret;
	}
}
