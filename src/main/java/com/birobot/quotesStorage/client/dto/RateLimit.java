package com.birobot.quotesStorage.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Rate limits.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RateLimit {

  private RateLimitType rateLimitType;

  private RateLimitInterval interval;

  private Integer limit;

  public RateLimitType getRateLimitType() {
    return rateLimitType;
  }

  public void setRateLimitType(RateLimitType rateLimitType) {
    this.rateLimitType = rateLimitType;
  }

  public RateLimitInterval getInterval() {
    return interval;
  }

  public void setInterval(RateLimitInterval interval) {
    this.interval = interval;
  }

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  @Override
  public String toString() {
    return "RateLimit{" +
            "rateLimitType=" + rateLimitType +
            ", interval=" + interval +
            ", limit=" + limit +
            '}';
  }
}
