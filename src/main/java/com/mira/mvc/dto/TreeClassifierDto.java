package com.mira.mvc.dto;

/**
 * Древовидный классификатор
 */
public class TreeClassifierDto<T extends TreeClassifierDto> extends ClassifierDto {
  protected T parent;

  public T getParent() {
    return parent;
  }

  public void setParent(T parent) {
    this.parent = parent;
  }
}
