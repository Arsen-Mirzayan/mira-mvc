package com.mira.mvc.service.impl;

import com.mira.jpa2.dao.DictionaryDao;
import com.mira.jpa2.data.DictionaryObject;
import com.mira.mvc.dto.DictionaryDto;
import com.mira.mvc.service.DictionaryService;
import org.dozer.Mapper;

import java.util.List;

public abstract class DictionaryServiceImpl<ENTITY extends DictionaryObject, SERVICE extends DictionaryDao<ENTITY>, DTO extends DictionaryDto>
    extends DefaultServiceImpl<ENTITY, SERVICE, DTO>
    implements DictionaryService<ENTITY, SERVICE, DTO> {

  public DictionaryServiceImpl(Mapper mapper, SERVICE dalService) {
    super(mapper, dalService);
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
