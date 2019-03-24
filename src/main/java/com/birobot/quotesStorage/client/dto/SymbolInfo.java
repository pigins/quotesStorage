package com.birobot.quotesStorage.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Symbol information (base/quote).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SymbolInfo {

  private String symbol;

  private SymbolStatus status;

  private String baseAsset;

  private Integer baseAssetPrecision;

  private String quoteAsset;

  private Integer quotePrecision;

  private List<OrderType> orderTypes;

  private boolean icebergAllowed;

  private List<SymbolFilter> filters;

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public SymbolStatus getStatus() {
    return status;
  }

  public void setStatus(SymbolStatus status) {
    this.status = status;
  }

  public String getBaseAsset() {
    return baseAsset;
  }

  public void setBaseAsset(String baseAsset) {
    this.baseAsset = baseAsset;
  }

  public Integer getBaseAssetPrecision() {
    return baseAssetPrecision;
  }

  public void setBaseAssetPrecision(Integer baseAssetPrecision) {
    this.baseAssetPrecision = baseAssetPrecision;
  }

  public String getQuoteAsset() {
    return quoteAsset;
  }

  public void setQuoteAsset(String quoteAsset) {
    this.quoteAsset = quoteAsset;
  }

  public Integer getQuotePrecision() {
    return quotePrecision;
  }

  public void setQuotePrecision(Integer quotePrecision) {
    this.quotePrecision = quotePrecision;
  }

  public List<OrderType> getOrderTypes() {
    return orderTypes;
  }

  public void setOrderTypes(List<OrderType> orderTypes) {
    this.orderTypes = orderTypes;
  }

  public boolean isIcebergAllowed() {
    return icebergAllowed;
  }

  public void setIcebergAllowed(boolean icebergAllowed) {
    this.icebergAllowed = icebergAllowed;
  }

  public List<SymbolFilter> getFilters() {
    return filters;
  }

  public void setFilters(List<SymbolFilter> filters) {
    this.filters = filters;
  }

  /**
   * @param filterType filter type to filter for.
   * @return symbol filter information for the provided filter type.
   */
  public SymbolFilter getSymbolFilter(FilterType filterType) {
    return filters.stream()
        .filter(symbolFilter -> symbolFilter.getFilterType() == filterType)
        .findFirst()
        .get();
  }

  @Override
  public String toString() {
    return "SymbolInfo{" +
            "symbol='" + symbol + '\'' +
            ", status=" + status +
            ", baseAsset='" + baseAsset + '\'' +
            ", baseAssetPrecision=" + baseAssetPrecision +
            ", quoteAsset='" + quoteAsset + '\'' +
            ", quotePrecision=" + quotePrecision +
            ", orderTypes=" + orderTypes +
            ", icebergAllowed=" + icebergAllowed +
            ", filters=" + filters +
            '}';
  }
}
