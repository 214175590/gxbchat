package com.echinacoop.utils;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 是一种可逆加密算法，对用户的敏感信息加密处理 对原始数据进行AES加密后，在进行Base64编码转化；
 */
public class AESUtils {

	/*
	 * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
	 */
	private String sKey = "Echinacoop#gxb";
	private String ivParameter = "0392039203920300";
	private static AESUtils instance = null;

	private AESUtils() {

	}

	public static AESUtils getInstance() {
		if (instance == null)
			instance = new AESUtils();
		return instance;
	}

	// 加密
	public String encrypt(String sSrc) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] raw16 = new byte[16];
		byte[] raw = sKey.getBytes();
		System.arraycopy(raw, 0, raw16, 0, raw.length); // 16位补全
		SecretKeySpec skeySpec = new SecretKeySpec(raw16, "AES");
		IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));

		return Base64.getEncoder().encodeToString(encrypted);// 此处使用BASE64做转码。
	}

	// 解密
	public String decrypt(String sSrc) throws Exception {
		try {

			byte[] raw = sKey.getBytes("ASCII");
			byte[] raw16 = new byte[16];

			System.arraycopy(raw, 0, raw16, 0, raw.length); // 16位补全
			SecretKeySpec skeySpec = new SecretKeySpec(raw16, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] encrypted1 = Base64.getDecoder().decode(sSrc);
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original, "utf-8");
			return originalString;
		} catch (Exception ex) {
			return null;
		}
	}

}