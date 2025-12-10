package com.zcpbysj.campusidletrade_server.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT令牌工具类
 * 提供JWT令牌的生成、验证、解析等功能
 * 用于用户登录认证和权限管理
 */
@Component
public class JwtUtil {

    // JWT密钥 - 在生产环境中应该从配置文件或环境变量中读取
    @Value("${jwt.secret:campusIdleTrade2024SecretKeyForJWTTokenGeneration}")
    private String secret;

    // JWT过期时间（毫秒） - 默认24小时
    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    // 刷新令牌过期时间（毫秒） - 默认7天
    @Value("${jwt.refresh-expiration:604800000}")
    private Long refreshExpiration;

    /**
     * 生成密钥
     * 使用HMAC-SHA算法生成安全的密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 从令牌中提取用户名
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从令牌中提取过期时间
     * @param token JWT令牌
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从令牌中提取用户ID
     * @param token JWT令牌
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从令牌中提取用户角色
     * @param token JWT令牌
     * @return 用户角色
     */
    public String getRoleFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    /**
     * 从令牌中提取指定的声明
     * @param token JWT令牌
     * @param claimsResolver 声明解析器
     * @return 声明值
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从令牌中提取所有声明
     * @param token JWT令牌
     * @return 所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("无法解析JWT令牌", e);
        }
    }

    /**
     * 检查令牌是否过期
     * @param token JWT令牌
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true; // 解析失败视为过期
        }
    }

    /**
     * 生成访问令牌
     * @param username 用户名
     * @param userId 用户ID
     * @param role 用户角色
     * @return JWT访问令牌
     */
    public String generateAccessToken(String username, Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("type", "access");
        return createToken(claims, username, expiration);
    }

    /**
     * 生成刷新令牌
     * @param username 用户名
     * @param userId 用户ID
     * @return JWT刷新令牌
     */
    public String generateRefreshToken(String username, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "refresh");
        return createToken(claims, username, refreshExpiration);
    }

    /**
     * 创建令牌
     * @param claims 声明
     * @param subject 主题（通常是用户名）
     * @param expiration 过期时间（毫秒）
     * @return JWT令牌
     */
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 验证令牌
     * @param token JWT令牌
     * @param username 用户名
     * @return 是否有效
     */
    public Boolean validateToken(String token, String username) {
        try {
            final String tokenUsername = getUsernameFromToken(token);
            return (tokenUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证令牌（不检查用户名）
     * @param token JWT令牌
     * @return 是否有效
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查令牌类型
     * @param token JWT令牌
     * @param expectedType 期望的类型（access/refresh）
     * @return 是否匹配
     */
    public Boolean isTokenType(String token, String expectedType) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            String tokenType = claims.get("type", String.class);
            return expectedType.equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从请求头中提取令牌
     * 支持 "Bearer token" 和直接的token格式
     * @param authHeader 认证头
     * @return 令牌字符串
     */
    public String extractTokenFromHeader(String authHeader) {
        if (StringUtil.isEmpty(authHeader)) {
            return null;
        }
        
        // 处理 "Bearer token" 格式
        if (authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        // 直接返回token
        return authHeader;
    }

    /**
     * 获取令牌剩余有效时间（秒）
     * @param token JWT令牌
     * @return 剩余秒数，-1表示已过期或无效
     */
    public Long getTokenRemainingTime(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            Date now = new Date();
            if (expiration.before(now)) {
                return -1L; // 已过期
            }
            return (expiration.getTime() - now.getTime()) / 1000;
        } catch (Exception e) {
            return -1L;
        }
    }

    /**
     * 刷新访问令牌
     * 使用刷新令牌生成新的访问令牌
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    public String refreshAccessToken(String refreshToken) {
        try {
            // 验证刷新令牌
            if (!validateToken(refreshToken) || !isTokenType(refreshToken, "refresh")) {
                throw new RuntimeException("无效的刷新令牌");
            }
            
            // 从刷新令牌中提取信息
            String username = getUsernameFromToken(refreshToken);
            Long userId = getUserIdFromToken(refreshToken);
            
            // 这里可以从数据库获取最新的用户角色
            // 暂时使用默认角色，实际项目中应该查询数据库
            String role = "USER";
            
            // 生成新的访问令牌
            return generateAccessToken(username, userId, role);
        } catch (Exception e) {
            throw new RuntimeException("刷新令牌失败", e);
        }
    }

    /**
     * 解析令牌获取用户信息
     * @param token JWT令牌
     * @return 用户信息Map
     */
    public Map<String, Object> parseTokenToUserInfo(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", claims.getSubject());
            userInfo.put("userId", claims.get("userId"));
            userInfo.put("role", claims.get("role"));
            userInfo.put("issuedAt", claims.getIssuedAt());
            userInfo.put("expiration", claims.getExpiration());
            return userInfo;
        } catch (Exception e) {
            throw new RuntimeException("解析令牌失败", e);
        }
    }

    /**
     * 生成令牌对（访问令牌 + 刷新令牌）
     * @param username 用户名
     * @param userId 用户ID
     * @param role 用户角色
     * @return 包含访问令牌和刷新令牌的Map
     */
    public Map<String, String> generateTokenPair(String username, Long userId, String role) {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", generateAccessToken(username, userId, role));
        tokens.put("refreshToken", generateRefreshToken(username, userId));
        return tokens;
    }
    
    /**
     * 生成简单令牌（用于普通用户登录）
     * @param userId 用户ID
     * @param studentId 学号
     * @return JWT令牌
     */
    public String generateToken(Long userId, String studentId) {
        return generateAccessToken(studentId, userId, "USER");
    }
    
    /**
     * 从请求中获取用户ID
     * @param request HTTP请求
     * @return 用户ID
     */
    public Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && !token.isEmpty()) {
            // 兼容 "Bearer xxx" 和直接 "xxx" 两种格式
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            try {
                return getUserIdFromToken(token);
            } catch (Exception e) {
                return null;
            }
        }
        // 如果没有token，尝试从请求属性中获取（可能由拦截器设置）
        Object userId = request.getAttribute("userId");
        if (userId != null) {
            if (userId instanceof Long) {
                return (Long) userId;
            }
            return Long.parseLong(userId.toString());
        }
        return null;
    }
}