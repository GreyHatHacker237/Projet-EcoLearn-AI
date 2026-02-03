package com.example.eco.interceptor;

import com.example.eco.config.RateLimitConfig;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RateLimitConfig rateLimitConfig;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        
        String key = getClientKey(request);
        Bucket bucket;
        
        // Endpoints sensibles (OpenAI)
        if (isSensitiveEndpoint(request.getRequestURI())) {
            bucket = rateLimitConfig.resolveSensitiveBucket(key);
        } else {
            bucket = rateLimitConfig.resolveBucket(key);
        }
        
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        
        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), 
                "Trop de requêtes. Réessayez dans " + waitForRefill + " secondes.");
            return false;
        }
    }

    /**
     * Génère une clé unique par client (IP ou userId)
     */
    private String getClientKey(HttpServletRequest request) {
        // Option 1: Par IP
        String clientIp = request.getRemoteAddr();
        
        // Option 2: Par userId (si authentifié)
        String userId = request.getHeader("X-User-Id");
        
        return userId != null ? "user-" + userId : "ip-" + clientIp;
    }

    /**
     * Détermine si l'endpoint est sensible
     */
    private boolean isSensitiveEndpoint(String uri) {
        return uri.contains("/learning/generate") || 
               uri.contains("/learning/personalize") ||
               uri.contains("/carbon/calculate");
    }
}