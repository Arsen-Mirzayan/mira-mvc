package com.mira.mvc.system.impl;


import com.mira.jpa2.data.DictionaryObject;
import com.mira.jpa2.service.DictionaryService;
import com.mira.mvc.dto.DictionaryDto;
import com.mira.mvc.system.DictionaryCRUDSystem;

import java.util.List;

public abstract class DictionaryCRUDSystemImpl<ENTITY extends DictionaryObject, SERVICE extends DictionaryService<ENTITY>, DTO extends DictionaryDto>
    extends DefaultCRUDSystemImpl<ENTITY, SERVICE, DTO>
    implements DictionaryCRUDSystem<ENTITY, SERVICE, DTO> {

  @Override
  public List<DTO> findByName(String name) {
    return convert(getDalService().findByName(name));
  }

  @Override
  public List<DTO> findOrCreate(String name) {
    return convert(getDalService().findOrCreate(name));
  }
}
