package com.mira.mvc.system;

import com.mira.jpa2.data.DefaultPersistentObject;
import com.mira.jpa2.service.DefaultDalService;
import com.mira.mvc.dto.EntityDto;

/**
 * Система интегрирует в себя CRUD операции для сущностей с простым числовым ключом
 */
public interface DefaultCRUDSystem<ENTITY extends DefaultPersistentObject, SERVICE extends DefaultDalService<ENTITY>, DTO extends EntityDto>
    extends DefaultSystem<ENTITY, SERVICE, DTO, Long, Long> {
}
