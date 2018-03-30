package com.mira.mvc.system;

import com.mira.jpa2.data.DefaultPersistentObject;
import com.mira.jpa2.service.DefaultDalService;
import com.mira.mvc.dto.EntityDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Система интегрирует в себя CRUD операции
 */
public abstract class DefaultCRUDSystem<ENTITY extends DefaultPersistentObject, SERVICE extends DefaultDalService<ENTITY>, DTO extends EntityDto>
    extends DefaultSystem<ENTITY, SERVICE, DTO, Long, Long> {

  @Override
  public Class<Long> getEntityIdClass() {
    return Long.class;
  }

  @Override
  protected Long convertId(Long dtoId) {
    return dtoId;
  }

  protected <T extends DefaultPersistentObject> T toEntity(EntityDto dto, Class<T> cl) {
    return super.toEntity(dto, cl, Long.class);
  }

  protected <T extends DefaultPersistentObject> List<T> toEntities(List<? extends EntityDto> dtos, Class<T> cl) {
    if (dtos == null) {
      return null;
    }
    List<T> result = new ArrayList<>(dtos.size());
    for (EntityDto dto : dtos) {
      result.add(toEntity(dto, cl));
    }
    return result;
  }

  /**
   * Находит хранимый объект, соответствующий указанному DTO, и обновляет его свойства. Либо сохраняет новый.
   *
   * @param object DTO объект
   * @return сохранённый объект
   */
  public DTO save(DTO object) {
    ENTITY entity = convertToEntity(object, getEntityClass());
    getDalService().save(entity);
    return convert(entity);
  }
}
