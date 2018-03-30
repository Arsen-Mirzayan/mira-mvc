package com.mira.mvc.dto;

/**
 * Общий классификатор
 */
public class ClassifierDto extends DictionaryDto {
  protected String code;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getFullName() {
    return code != null && name != null ? String.format("%s %s", code, name) : null;
  }
}
