package com.nqvinh.cryptotradingsystem.filter;

import reactor.core.publisher.Mono;

/**
 * Helper to read client IP from Reactor context (set by {@link ClientIpLoggingFilter}). Use when
 * logging inside a reactive chain and you need the client IP.
 *
 * <pre>
 * return someService.doSomething()
 *     .doOnSuccess(result -> ReactorContextClientIp.getCurrent()
 *         .subscribe(ip -> log.info("action completed clientIp={}", ip)));
 * </pre>
 */
public final class ReactorContextClientIp {

  private ReactorContextClientIp() {}

  /** Get client IP from the current Reactor context, or "unknown" if not set. */
  public static Mono<String> getCurrent() {
    return Mono.deferContextual(
        ctx -> {
          String ip = ctx.getOrDefault(ClientIpLoggingFilter.CLIENT_IP_ATTRIBUTE, "unknown");
          return Mono.just(ip != null ? ip : "unknown");
        });
  }
}
