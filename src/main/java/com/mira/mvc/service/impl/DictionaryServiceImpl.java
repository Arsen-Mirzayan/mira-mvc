package com.mira.mvc.service.impl;

import com.mira.jpa2.dao.DictionaryDao;
import com.mira.jpa2.data.DictionaryObject;
import com.mira.mvc.dto.DictionaryDto;
import com.mira.mvc.service.DictionaryService;

import java.util.List;

public abstract class DictionaryServiceImpl<ENTITY extends DictionaryObject, SERVICE extends DictionaryDao<ENTITY>, DTO extends DictionaryDto>
    extends DefaultCRUDServiceImpl<ENTITY, SERVICE, DTO>
    implements DictionaryService<ENTITY, SERVICE, DTO> {

  @Override
  public List<DTO> findByName(String name) {
    return convert(getDalService().findByName(name));
  }

  @Override
  public List<DTO> findOrCreate(String name) {
    return convert(getDalService().findOrCreate(name));
  }
}
