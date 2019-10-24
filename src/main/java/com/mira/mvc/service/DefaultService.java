package com.mira.mvc.service;

import com.mira.jpa2.dao.DefaultDao;
import com.mira.jpa2.data.DefaultPersistentObject;
import com.mira.mvc.dto.EntityDto;

/**
 * Система интегрирует в себя CRUD операции для сущностей с простым числовым ключом
 */
public interface DefaultService<ENTITY extends DefaultPersistentObject, SERVICE extends DefaultDao<ENTITY>, DTO extends EntityDto>
    extends AbstractService<ENTITY, SERVICE, DTO, Long, Long> {
}
