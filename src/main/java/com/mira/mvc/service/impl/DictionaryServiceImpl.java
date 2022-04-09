package com.mira.mvc.service.impl;

import com.mira.jpa2.dao.DictionaryDao;
import com.mira.jpa2.data.DictionaryObject;
import com.mira.mvc.dto.DictionaryDto;
import com.mira.mvc.service.DictionaryService;
import com.mira.mvc.validation.ValidationService;
import org.dozer.Mapper;

import java.util.List;

public abstract class DictionaryServiceImpl<ENTITY extends DictionaryObject, SERVICE extends DictionaryDao<ENTITY>, DTO extends DictionaryDto>
    extends DefaultServiceImpl<ENTITY, SERVICE, DTO>
    implements DictionaryService<ENTITY, DTO> {

  public DictionaryServiceImpl(Mapper mapper, ValidationService validationService, SERVICE dalService) {
    super(mapper, validationService, dalService);
  }

  @Override
  public List<DTO> findByName(String name) {
    return convert(getDalService().findByName(name));
  }

  @Override
  public List<DTO> findOrCreate(String name) {
    return convert(getDalService().findOrCreate(name));
  }
}
