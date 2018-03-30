package com.mira.mvc.dto;

/**
 * Dto для словарей
 */
public class DictionaryDto extends EntityDto {
  protected String name;
  protected String transliteratedNameForUri;

  public String getTransliteratedNameForUri() {
    return transliteratedNameForUri;
  }

  public void setTransliteratedNameForUri(String transliteratedNameForUri) {
    this.transliteratedNameForUri = transliteratedNameForUri;
  }

  public DictionaryDto() {
  }

  public DictionaryDto(String name) {
    this.name = name;
  }

  public DictionaryDto(Long id, String name) {
    super(id);
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
