package com.mira.mvc.system.impl;

import com.mira.jpa2.data.DefaultPersistentObject;
import com.mira.jpa2.service.DefaultDalService;
import com.mira.mvc.dto.EntityDto;
import com.mira.mvc.system.DefaultCRUDSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Система интегрирует в себя CRUD операции
 */
public abstract class DefaultCRUDSystemImpl<
    ENTITY extends DefaultPersistentObject
    , SERVICE extends DefaultDalService<ENTITY>, DTO extends EntityDto
    >
    extends DefaultSystemImpl<ENTITY, SERVICE, DTO, Long, Long>

    implements DefaultCRUDSystem<ENTITY, SERVICE, DTO> {

  @Override
  public Class<Long> getEntityIdClass() {
    return Long.class;
  }

  @Override
  protected Long convertId(Long dtoId) {
    return dtoId;
  }

  @Override
  protected Long extractId(DTO dto) {
    return dto.getId();
  }
}
