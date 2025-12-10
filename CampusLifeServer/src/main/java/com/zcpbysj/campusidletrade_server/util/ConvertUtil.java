package com.zcpbysj.campusidletrade_server.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * 数据转换工具类
 * 提供JSON、XML、类型转换等功能
 */
public class ConvertUtil {

    private static final ObjectMapper objectMapper;
    
    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // ==================== JSON 转换 ====================

    /**
     * 对象转JSON字符串
     */
    public static String toJson(Object obj) {
        if (obj == null) return null;
        
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("对象转JSON失败", e);
        }
    }

    /**
     * 对象转格式化的JSON字符串
     */
    public static String toPrettyJson(Object obj) {
        if (obj == null) return null;
        
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("对象转JSON失败", e);
        }
    }

    /**
     * JSON字符串转对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtil.isEmpty(json)) return null;
        
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON转对象失败", e);
        }
    }

    /**
     * JSON字符串转对象（支持泛型）
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (StringUtil.isEmpty(json)) return null;
        
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON转对象失败", e);
        }
    }

    /**
     * JSON字符串转List
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        if (StringUtil.isEmpty(json)) return new ArrayList<>();
        
        try {
            return objectMapper.readValue(json, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON转List失败", e);
        }
    }

    /**
     * JSON字符串转Map
     */
    public static Map<String, Object> fromJsonToMap(String json) {
        if (StringUtil.isEmpty(json)) return new HashMap<>();
        
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON转Map失败", e);
        }
    }

    // ==================== XML 转换 ====================

    /**
     * Map转XML字符串
     */
    public static String mapToXml(Map<String, Object> map, String rootElementName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            
            Element rootElement = document.createElement(rootElementName);
            document.appendChild(rootElement);
            
            buildXmlElement(document, rootElement, map);
            
            return documentToString(document);
        } catch (Exception e) {
            throw new RuntimeException("Map转XML失败", e);
        }
    }

    /**
     * XML字符串转Map
     */
    public static Map<String, Object> xmlToMap(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xml)));
            
            Element rootElement = document.getDocumentElement();
            return parseXmlElement(rootElement);
        } catch (Exception e) {
            throw new RuntimeException("XML转Map失败", e);
        }
    }

    /**
     * 构建XML元素
     */
    private static void buildXmlElement(Document document, Element parent, Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            Element element = document.createElement(key);
            
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) value;
                buildXmlElement(document, element, nestedMap);
            } else if (value instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) value;
                for (Object item : list) {
                    Element itemElement = document.createElement("item");
                    if (item instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> itemMap = (Map<String, Object>) item;
                        buildXmlElement(document, itemElement, itemMap);
                    } else {
                        itemElement.setTextContent(String.valueOf(item));
                    }
                    element.appendChild(itemElement);
                }
            } else {
                element.setTextContent(value != null ? String.valueOf(value) : "");
            }
            
            parent.appendChild(element);
        }
    }

    /**
     * 解析XML元素
     */
    private static Map<String, Object> parseXmlElement(Element element) {
        Map<String, Object> map = new HashMap<>();
        NodeList nodeList = element.getChildNodes();
        
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                String key = childElement.getTagName();
                
                if (childElement.hasChildNodes() && 
                    childElement.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {
                    // 有子元素
                    Object existingValue = map.get(key);
                    if (existingValue instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<Object> list = (List<Object>) existingValue;
                        list.add(parseXmlElement(childElement));
                    } else if (existingValue != null) {
                        List<Object> list = new ArrayList<>();
                        list.add(existingValue);
                        list.add(parseXmlElement(childElement));
                        map.put(key, list);
                    } else {
                        map.put(key, parseXmlElement(childElement));
                    }
                } else {
                    // 文本内容
                    String textContent = childElement.getTextContent();
                    Object existingValue = map.get(key);
                    if (existingValue instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<Object> list = (List<Object>) existingValue;
                        list.add(textContent);
                    } else if (existingValue != null) {
                        List<Object> list = new ArrayList<>();
                        list.add(existingValue);
                        list.add(textContent);
                        map.put(key, list);
                    } else {
                        map.put(key, textContent);
                    }
                }
            }
        }
        
        return map;
    }

    /**
     * Document转字符串
     */
    private static String documentToString(Document document) throws Exception {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        
        return writer.toString();
    }

    // ==================== 类型转换 ====================

    /**
     * 安全的字符串转整数
     */
    public static Integer toInteger(String str) {
        if (StringUtil.isEmpty(str)) return null;
        
        try {
            return Integer.valueOf(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 安全的字符串转整数（带默认值）
     */
    public static int toInteger(String str, int defaultValue) {
        Integer result = toInteger(str);
        return result != null ? result : defaultValue;
    }

    /**
     * 安全的字符串转长整数
     */
    public static Long toLong(String str) {
        if (StringUtil.isEmpty(str)) return null;
        
        try {
            return Long.valueOf(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 安全的字符串转长整数（带默认值）
     */
    public static long toLong(String str, long defaultValue) {
        Long result = toLong(str);
        return result != null ? result : defaultValue;
    }

    /**
     * 安全的字符串转双精度浮点数
     */
    public static Double toDouble(String str) {
        if (StringUtil.isEmpty(str)) return null;
        
        try {
            return Double.valueOf(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 安全的字符串转双精度浮点数（带默认值）
     */
    public static double toDouble(String str, double defaultValue) {
        Double result = toDouble(str);
        return result != null ? result : defaultValue;
    }

    /**
     * 安全的字符串转BigDecimal
     */
    public static BigDecimal toBigDecimal(String str) {
        if (StringUtil.isEmpty(str)) return null;
        
        try {
            return new BigDecimal(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 安全的字符串转布尔值
     */
    public static Boolean toBoolean(String str) {
        if (StringUtil.isEmpty(str)) return null;
        
        String trimmed = str.trim().toLowerCase();
        if ("true".equals(trimmed) || "1".equals(trimmed) || "yes".equals(trimmed)) {
            return true;
        } else if ("false".equals(trimmed) || "0".equals(trimmed) || "no".equals(trimmed)) {
            return false;
        }
        
        return null;
    }

    /**
     * 安全的字符串转布尔值（带默认值）
     */
    public static boolean toBoolean(String str, boolean defaultValue) {
        Boolean result = toBoolean(str);
        return result != null ? result : defaultValue;
    }

    // ==================== 数字格式化 ====================

    /**
     * 格式化数字（保留指定小数位）
     */
    public static String formatNumber(double number, int decimalPlaces) {
        String pattern = "#." + StringUtil.repeat("0", decimalPlaces);
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(number);
    }

    /**
     * 格式化百分比
     */
    public static String formatPercentage(double value, int decimalPlaces) {
        BigDecimal bd = BigDecimal.valueOf(value * 100);
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return bd + "%";
    }

    /**
     * 格式化货币
     */
    public static String formatCurrency(double amount) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return "¥" + df.format(amount);
    }

    // ==================== 单位转换 ====================

    /**
     * 字节转换为可读格式
     */
    public static String formatBytes(long bytes) {
        return FileUtil.formatFileSize(bytes);
    }

    /**
     * 温度转换：摄氏度转华氏度
     */
    public static double celsiusToFahrenheit(double celsius) {
        return celsius * 9.0 / 5.0 + 32;
    }

    /**
     * 温度转换：华氏度转摄氏度
     */
    public static double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32) * 5.0 / 9.0;
    }

    /**
     * 长度转换：米转英尺
     */
    public static double metersToFeet(double meters) {
        return meters * 3.28084;
    }

    /**
     * 长度转换：英尺转米
     */
    public static double feetToMeters(double feet) {
        return feet / 3.28084;
    }

    /**
     * 重量转换：千克转磅
     */
    public static double kilogramsToPounds(double kilograms) {
        return kilograms * 2.20462;
    }

    /**
     * 重量转换：磅转千克
     */
    public static double poundsToKilograms(double pounds) {
        return pounds / 2.20462;
    }

    // ==================== 进制转换 ====================

    /**
     * 十进制转二进制
     */
    public static String decimalToBinary(int decimal) {
        return Integer.toBinaryString(decimal);
    }

    /**
     * 十进制转八进制
     */
    public static String decimalToOctal(int decimal) {
        return Integer.toOctalString(decimal);
    }

    /**
     * 十进制转十六进制
     */
    public static String decimalToHex(int decimal) {
        return Integer.toHexString(decimal).toUpperCase();
    }

    /**
     * 二进制转十进制
     */
    public static int binaryToDecimal(String binary) {
        return Integer.parseInt(binary, 2);
    }

    /**
     * 八进制转十进制
     */
    public static int octalToDecimal(String octal) {
        return Integer.parseInt(octal, 8);
    }

    /**
     * 十六进制转十进制
     */
    public static int hexToDecimal(String hex) {
        return Integer.parseInt(hex, 16);
    }

    // ==================== 集合转换 ====================

    /**
     * 数组转List
     */
    public static <T> List<T> arrayToList(T[] array) {
        if (array == null) return new ArrayList<>();
        return Arrays.asList(array);
    }

    /**
     * List转数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] listToArray(List<T> list, Class<T> clazz) {
        if (list == null || list.isEmpty()) {
            return (T[]) java.lang.reflect.Array.newInstance(clazz, 0);
        }
        return list.toArray((T[]) java.lang.reflect.Array.newInstance(clazz, list.size()));
    }

    /**
     * 字符串数组转整数List
     */
    public static List<Integer> stringArrayToIntegerList(String[] array) {
        if (array == null) return new ArrayList<>();
        
        return Arrays.stream(array)
                    .map(ConvertUtil::toInteger)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
    }

    /**
     * 整数List转字符串数组
     */
    public static String[] integerListToStringArray(List<Integer> list) {
        if (list == null) return new String[0];
        
        return list.stream()
                  .map(String::valueOf)
                  .toArray(String[]::new);
    }

    /**
     * Map的key-value互换
     */
    public static <K, V> Map<V, K> reverseMap(Map<K, V> originalMap) {
        if (originalMap == null) return new HashMap<>();
        
        Map<V, K> reversedMap = new HashMap<>();
        for (Map.Entry<K, V> entry : originalMap.entrySet()) {
            reversedMap.put(entry.getValue(), entry.getKey());
        }
        return reversedMap;
    }

    // ==================== 日期转换 ====================

    /**
     * 字符串转LocalDate
     */
    public static LocalDate toLocalDate(String dateStr, String pattern) {
        if (StringUtil.isEmpty(dateStr)) return null;
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 字符串转LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(String dateTimeStr, String pattern) {
        if (StringUtil.isEmpty(dateTimeStr)) return null;
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * LocalDate转字符串
     */
    public static String fromLocalDate(LocalDate date, String pattern) {
        if (date == null) return null;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }

    /**
     * LocalDateTime转字符串
     */
    public static String fromLocalDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) return null;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }
}