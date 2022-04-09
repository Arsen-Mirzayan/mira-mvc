package com.mira.mvc.service.impl;

import com.mira.jpa2.Orders;
import com.mira.jpa2.PageRequest;
import com.mira.jpa2.PageResponse;
import com.mira.jpa2.dao.ClassifierDao;
import com.mira.jpa2.data.Classifier;
import com.mira.jpa2.data.Classifier_;
import com.mira.mvc.dto.ClassifierDto;
import com.mira.mvc.service.ClassifierService;
import com.mira.mvc.validation.ValidationService;
import org.dozer.Mapper;

/**
 * Родительский класс для систем работы с классификаторами
 *
 * @param <ENTITY>  класс сущности
 * @param <SERVICE> класс сервиса для работы с сущностью
 * @param <DTO>     класс DTO
 */
public abstract class ClassifierServiceImpl<ENTITY extends Classifier, SERVICE extends ClassifierDao<ENTITY>, DTO extends ClassifierDto>
    extends DictionaryServiceImpl<ENTITY, SERVICE, DTO>
    implements ClassifierService<ENTITY, DTO> {

  public ClassifierServiceImpl(Mapper mapper, ValidationService validationService, SERVICE dalService) {
    super(mapper, validationService, dalService);
  }

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
