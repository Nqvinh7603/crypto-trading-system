package com.nqvinh.cryptotradingsystem.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ClientIpLoggingFilter implements WebFilter {

  public static final String CLIENT_IP_ATTRIBUTE = "clientIp";
  public static final String REQUEST_START_TIME_ATTRIBUTE = "requestStartTime";

  private static final String X_FORWARDED_FOR = "X-Forwarded-For";
  private static final String X_REAL_IP = "X-Real-IP";

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String clientIp = resolveClientIp(exchange.getRequest());
    exchange.getAttributes().put(CLIENT_IP_ATTRIBUTE, clientIp);
    exchange.getAttributes().put(REQUEST_START_TIME_ATTRIBUTE, System.currentTimeMillis());

    String method = exchange.getRequest().getMethod().name();
    String path = exchange.getRequest().getURI().getPath();
    String query = exchange.getRequest().getURI().getRawQuery();
    String pathWithQuery = query != null && !query.isBlank() ? path + "?" + query : path;

    log.info("Incoming request method={} path={} clientIp={}", method, pathWithQuery, clientIp);

    return chain
        .filter(exchange)
        .contextWrite(ctx -> ctx.put(CLIENT_IP_ATTRIBUTE, clientIp))
        .doFinally(signalType -> logCompletion(exchange, clientIp, method, pathWithQuery))
        .doOnError(
            t ->
                log.warn(
                    "Request failed method={} path={} clientIp={} error={}",
                    method,
                    pathWithQuery,
                    clientIp,
                    t.getMessage()));
  }

  private void logCompletion(
      ServerWebExchange exchange, String clientIp, String method, String pathWithQuery) {
    Long startTime = (Long) exchange.getAttributes().get(REQUEST_START_TIME_ATTRIBUTE);
    long durationMs = startTime != null ? System.currentTimeMillis() - startTime : -1;
    int status =
        exchange.getResponse().getStatusCode() != null
            ? exchange.getResponse().getStatusCode().value()
            : 0;
    log.info(
        "Completed request method={} path={} clientIp={} status={} durationMs={}",
        method,
        pathWithQuery,
        clientIp,
        status,
        durationMs);
  }

  /** Resolve client IP: X-Forwarded-For (first hop), then X-Real-IP, then remote address. */
  public static String resolveClientIp(ServerHttpRequest request) {
    List<String> forwardedFor = request.getHeaders().get(X_FORWARDED_FOR);
    if (forwardedFor != null && !forwardedFor.isEmpty()) {
      String first = forwardedFor.getFirst();
      if (first != null && !first.isBlank()) {
        // Can be "client, proxy1, proxy2" – first is origin client
        int comma = first.indexOf(',');
        String ip = comma > 0 ? first.substring(0, comma).trim() : first.trim();
        if (!ip.isEmpty()) return ip;
      }
    }
    String realIp = request.getHeaders().getFirst(X_REAL_IP);
    if (realIp != null && !realIp.isBlank()) return realIp.trim();
    InetSocketAddress remote = request.getRemoteAddress();
    if (remote != null && remote.getAddress() != null) return remote.getAddress().getHostAddress();
    return "unknown";
  }
}
