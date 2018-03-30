package com.mira.mvc.dto;

/**
 * Страница
 */
public class PageDto {
  protected String title;
  protected String keywords;
  protected String description;

  public PageDto() {
  }

  public PageDto(String title, String keywords, String description) {
    setTitle(title);
    setKeywords(keywords);
    setDescription(description);
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = normalize(title);
  }

  public String getKeywords() {
    return keywords;
  }

  public void setKeywords(String keywords) {
    this.keywords = normalize(keywords);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = normalize(description);
  }

  protected String normalize(String source) {
    return source != null ? source.replace("\"", "") : null;
  }
}
