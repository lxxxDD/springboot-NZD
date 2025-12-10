package com.zcpbysj.campusidletrade_server.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * 字符串工具类
 * 提供字符串验证、转换、加密、生成等功能
 */
public class StringUtil {

    // 常用正则表达式
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
    private static final String ID_CARD_REGEX = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$";
    
    // 随机字符集
    private static final String NUMBERS = "0123456789";
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    
    private static final Random random = new Random();

    /**
     * 检查字符串是否为空或null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 检查字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 验证邮箱格式
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return false;
        return Pattern.matches(EMAIL_REGEX, email);
    }

    /**
     * 验证手机号格式
     */
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) return false;
        return Pattern.matches(PHONE_REGEX, phone);
    }

    /**
     * 验证身份证号格式
     */
    public static boolean isValidIdCard(String idCard) {
        if (isEmpty(idCard)) return false;
        return Pattern.matches(ID_CARD_REGEX, idCard);
    }

    /**
     * 验证密码强度（至少8位，包含大小写字母和数字）
     */
    public static boolean isValidPassword(String password) {
        if (isEmpty(password)) return false;
        return Pattern.matches(PASSWORD_REGEX, password);
    }

    /**
     * 驼峰命名转下划线
     */
    public static String camelToUnderscore(String camelCase) {
        if (isEmpty(camelCase)) return camelCase;
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    /**
     * 下划线转驼峰命名
     */
    public static String underscoreToCamel(String underscore) {
        if (isEmpty(underscore)) return underscore;
        
        StringBuilder result = new StringBuilder();
        String[] parts = underscore.split("_");
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i == 0) {
                result.append(part.toLowerCase());
            } else {
                result.append(part.substring(0, 1).toUpperCase())
                      .append(part.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    /**
     * 首字母大写
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 首字母小写
     */
    public static String uncapitalize(String str) {
        if (isEmpty(str)) return str;
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * MD5加密
     */
    public static String md5(String input) {
        if (isEmpty(input)) return null;
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }

    /**
     * SHA256加密
     */
    public static String sha256(String input) {
        if (isEmpty(input)) return null;
        
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256算法不可用", e);
        }
    }

    /**
     * Base64编码
     */
    public static String base64Encode(String input) {
        if (isEmpty(input)) return null;
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64解码
     */
    public static String base64Decode(String encoded) {
        if (isEmpty(encoded)) return null;
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encoded);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Base64解码失败", e);
        }
    }

    /**
     * 生成随机数字字符串
     */
    public static String generateRandomNumbers(int length) {
        return generateRandomString(NUMBERS, length);
    }

    /**
     * 生成随机字母字符串
     */
    public static String generateRandomLetters(int length) {
        return generateRandomString(LETTERS, length);
    }

    /**
     * 生成随机字母数字字符串
     */
    public static String generateRandomAlphanumeric(int length) {
        return generateRandomString(NUMBERS + LETTERS, length);
    }

    /**
     * 生成随机密码（包含字母、数字、符号）
     */
    public static String generateRandomPassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("密码长度至少为8位");
        }
        return generateRandomString(NUMBERS + LETTERS + SYMBOLS, length);
    }

    /**
     * 生成验证码
     */
    public static String generateVerificationCode(int length) {
        return generateRandomNumbers(length);
    }

    /**
     * 从指定字符集生成随机字符串
     */
    private static String generateRandomString(String charset, int length) {
        if (length <= 0) return "";
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charset.length());
            sb.append(charset.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 手机号脱敏
     */
    public static String maskPhone(String phone) {
        if (isEmpty(phone) || phone.length() != 11) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 邮箱脱敏
     */
    public static String maskEmail(String email) {
        if (isEmpty(email) || !email.contains("@")) return email;
        
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        
        if (username.length() <= 2) {
            return "*".repeat(username.length()) + "@" + domain;
        } else {
            return username.substring(0, 2) + "*".repeat(username.length() - 2) + "@" + domain;
        }
    }

    /**
     * 身份证号脱敏
     */
    public static String maskIdCard(String idCard) {
        if (isEmpty(idCard) || idCard.length() != 18) return idCard;
        return idCard.substring(0, 6) + "********" + idCard.substring(14);
    }

    /**
     * 字符串模糊匹配（计算相似度）
     */
    public static double similarity(String str1, String str2) {
        if (str1 == null && str2 == null) return 1.0;
        if (str1 == null || str2 == null) return 0.0;
        if (str1.equals(str2)) return 1.0;
        
        int maxLength = Math.max(str1.length(), str2.length());
        if (maxLength == 0) return 1.0;
        
        int distance = levenshteinDistance(str1, str2);
        return 1.0 - (double) distance / maxLength;
    }

    /**
     * 计算编辑距离（Levenshtein距离）
     */
    private static int levenshteinDistance(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];
        
        for (int i = 0; i <= str1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= str2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]) + 1;
                }
            }
        }
        
        return dp[str1.length()][str2.length()];
    }

    /**
     * 移除HTML标签
     */
    public static String removeHtmlTags(String html) {
        if (isEmpty(html)) return html;
        return html.replaceAll("<[^>]+>", "");
    }

    /**
     * 转义HTML特殊字符
     */
    public static String escapeHtml(String html) {
        if (isEmpty(html)) return html;
        
        return html.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#x27;");
    }

    /**
     * 截取字符串（支持中文）
     */
    public static String truncate(String str, int maxLength, String suffix) {
        if (isEmpty(str) || str.length() <= maxLength) return str;
        
        if (suffix == null) suffix = "...";
        int realMaxLength = maxLength - suffix.length();
        
        if (realMaxLength <= 0) return suffix;
        
        return str.substring(0, realMaxLength) + suffix;
    }

    /**
     * 重复字符串
     */
    public static String repeat(String str, int count) {
        if (isEmpty(str) || count <= 0) return "";
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}