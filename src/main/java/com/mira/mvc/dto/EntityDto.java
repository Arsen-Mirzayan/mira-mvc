package com.mira.mvc.dto;

/**
 * Родительский DTO содержит первичный ключ и определённые через него equals и hascode
 */
public class EntityDto extends AbstractEntityDto<Long> {
  protected Long id;

  public EntityDto() {
  }

  public EntityDto(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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
