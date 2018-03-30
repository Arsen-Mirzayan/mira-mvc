package com.mira.mvc.dto;

/**
 * Абстрактный класс для DTO
 */
public abstract class AbstractEntityDto<IdClass> {
  public abstract IdClass getId();

  public abstract void setId(IdClass id);

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AbstractEntityDto that = (AbstractEntityDto) o;

    return !(getId() != null ? !getId().equals(that.getId()) : that.getId() != null);
  }

  @Override
  public int hashCode() {
    return getId() != null ? getId().hashCode() : 0;
  }
}
