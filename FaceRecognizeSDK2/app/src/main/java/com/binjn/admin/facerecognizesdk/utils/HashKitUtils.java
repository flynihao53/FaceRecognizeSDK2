/*
 * This file is part of fes of Faceyes
 * Created by gaoxiang on 17-4-24 下午6:30
 *
 * This software, including documentation, is protected by copyright controlled
 * Copyright (c) 2017. Kaytion I&T Co.Ltd All rights reserved.
 *
 * Last modified on 17-4-24 下午6:30
 */

package com.binjn.admin.facerecognizesdk.utils;

import java.security.MessageDigest;

/**
 * Faceyes
 *
 * @author gaoxiang
 * @version 1.0.0
 * @since JDK 1.8
 */
public class HashKitUtils {

    private static final java.security.SecureRandom random = new java.security.SecureRandom();
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
    private static final char[] CHAR_ARRAY = "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static String md5(String srcStr){
        return hash("MD5", srcStr);
    }
    public static String md5(byte[] srcBytes){
        return hash("MD5", srcBytes);
    }

    public static String sha1(String srcStr){
        return hash("SHA-1", srcStr);
    }
    public static String sha1(byte[] srcBytes){ return hash("SHA-1", srcBytes);}

    public static String sha256(String srcStr){
        return hash("SHA-256", srcStr);
    }
    public static String sha256(byte[] srcBytes){
        return hash("SHA-256", srcBytes);
    }

    public static String sha384(String srcStr){
        return hash("SHA-384", srcStr);
    }
    public static String sha384(byte[] srcBytes){
        return hash("SHA-384", srcBytes);
    }

    public static String sha512(String srcStr){
        return hash("SHA-512", srcStr);
    }
    public static String sha512(byte[] srcBytes){
        return hash("SHA-512", srcBytes);
    }

    public static String hash(String algorithm, String srcStr) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = md.digest(srcStr.getBytes("utf-8"));
            return toHex(bytes);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String hash(String algorithm, byte[] srcBytes) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = md.digest(srcBytes);
            return toHex(bytes);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toHex(byte[] bytes) {
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i=0; i<bytes.length; i++) {
            ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
            ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }

    /**
     * md5 128bit 16bytes
     * sha1 160bit 20bytes
     * sha256 256bit 32bytes
     * sha384 384bit 48bytes
     * sha512 512bit 64bytes
     */
    public static String generateSalt(int saltLength) {
        StringBuilder salt = new StringBuilder();
        for (int i=0; i<saltLength; i++) {
            salt.append(CHAR_ARRAY[random.nextInt(CHAR_ARRAY.length)]);
        }
        return salt.toString();
    }

    public static String generateSaltForSha256() {
        return generateSalt(32);
    }

    public static String generateSaltForSha512() {
        return generateSalt(64);
    }

    public static boolean slowEquals(byte[] a, byte[] b) {
        if (a == null || b == null) {
            return false;
        }

        int diff = a.length ^ b.length;
        for(int i=0; i<a.length && i<b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}
