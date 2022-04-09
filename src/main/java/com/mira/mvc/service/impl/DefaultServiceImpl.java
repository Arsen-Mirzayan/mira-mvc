package com.mira.mvc.service.impl;

import com.mira.jpa2.dao.DefaultDao;
import com.mira.jpa2.data.DefaultPersistentObject;
import com.mira.mvc.dto.EntityDto;
import com.mira.mvc.service.DefaultService;
import com.mira.mvc.validation.ValidationService;
import org.dozer.Mapper;

/**
 * Система интегрирует в себя CRUD операции
 */
public abstract class DefaultServiceImpl<
  ENTITY extends DefaultPersistentObject
  , SERVICE extends DefaultDao<ENTITY>, DTO extends EntityDto
  >
  extends AbstractServiceImpl<ENTITY, SERVICE, DTO, Long, Long>

  implements DefaultService<ENTITY, DTO> {

  public DefaultServiceImpl(Mapper mapper, ValidationService validationService, SERVICE dalService) {
    super(mapper, validationService, dalService);
  }

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
