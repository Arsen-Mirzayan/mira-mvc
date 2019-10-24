package com.mira.mvc.service.impl;

import com.mira.jpa2.dao.DefaultDao;
import com.mira.jpa2.data.DefaultPersistentObject;
import com.mira.mvc.dto.EntityDto;
import com.mira.mvc.service.DefaultCRUDService;

/**
 * Система интегрирует в себя CRUD операции
 */
public abstract class DefaultCRUDServiceImpl<
    ENTITY extends DefaultPersistentObject
    , SERVICE extends DefaultDao<ENTITY>, DTO extends EntityDto
    >
    extends DefaultServiceImpl<ENTITY, SERVICE, DTO, Long, Long>

    implements DefaultCRUDService<ENTITY, SERVICE, DTO> {

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
