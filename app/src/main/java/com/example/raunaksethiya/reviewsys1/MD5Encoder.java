package com.example.raunaksethiya.reviewsys1;

/**
 * Created by Raunak Sethiya on 14-Sep-16.
 */
public class MD5Encoder {

    private String toConvert;

    public MD5Encoder(String toConvert) {
        this.toConvert = toConvert;
    }

    public String encodeToMD5() {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(toConvert.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException ignored) {
        }
        return null;
    }
}
