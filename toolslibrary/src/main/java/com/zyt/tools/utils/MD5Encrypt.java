/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zyt.tools.utils;

import java.security.MessageDigest;

/*
 * MD5 算法
 */
public class MD5Encrypt {

	public MD5Encrypt() {
		
	}

	/**
	 * md5 加密:(默认UTF-8编码)
	 * 
	 * @param code
	 *            要加密的数据
	 * @return
	 */
	public static String md5Code(String code) {
		return md5Code(code, "UTF-8");
	}

	/**
	 * md5 加密:
	 * 
	 * @param code
	 *            要加密的数据
	 * @param charsetName
	 *            字符编码
	 * @return
	 */
	public static String md5Code(String code, String charsetName) {
		StringBuffer sb = new StringBuffer(32);
		if (code != null && !"".equals(code)) {
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] array = md.digest(code.getBytes(charsetName));

				for (int i = 0; i < array.length; i++) {
					sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString().toUpperCase();
	}
}
