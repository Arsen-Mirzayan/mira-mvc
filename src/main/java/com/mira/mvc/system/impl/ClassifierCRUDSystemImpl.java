package com.mira.mvc.system.impl;

import com.mira.jpa2.Orders;
import com.mira.jpa2.PageRequest;
import com.mira.jpa2.PageResponse;
import com.mira.jpa2.data.Classifier;
import com.mira.jpa2.data.Classifier_;
import com.mira.jpa2.service.ClassifierService;
import com.mira.mvc.dto.ClassifierDto;
import com.mira.mvc.system.ClassifierCRUDSystem;

/**
 * Родительский класс для систем работы с классификаторами
 *
 * @param <ENTITY>  класс сущности
 * @param <SERVICE> класс сервиса для работы с сущностью
 * @param <DTO>     класс DTO
 */
public abstract class ClassifierCRUDSystemImpl<ENTITY extends Classifier, SERVICE extends ClassifierService<ENTITY>, DTO extends ClassifierDto>
    extends DictionaryCRUDSystemImpl<ENTITY, SERVICE, DTO>
    implements ClassifierCRUDSystem<ENTITY, SERVICE, DTO> {
  @Override
  public DTO findByCode(String code) {
    return convert(getDalService().findByCode(code));
  }

  @Override
  public PageResponse<DTO> search(String code, String name, long page, long pageSize) {
    PageRequest<ENTITY> request = new PageRequest<>(page, pageSize, new Orders<>(Classifier_.code));
    return convert(getDalService().search(code, name, request));
  }

  @Override
  public DTO findOrCreate(String code, String name) {
    return convert(getDalService().findOrCreate(code, name));
  }

}
