package com.mira.mvc.system;


import com.mira.jpa2.data.DictionaryObject;
import com.mira.jpa2.service.DictionaryService;
import com.mira.mvc.dto.DictionaryDto;

import java.util.List;

public abstract class DictionaryCRUDSystem<ENTITY extends DictionaryObject, SERVICE extends DictionaryService<ENTITY>, DTO extends DictionaryDto>
    extends DefaultCRUDSystem<ENTITY, SERVICE, DTO> {

  /**
   * Находит элемент справочника по имени
   *
   * @param name имя элемента справочника
   * @return найденные элементы или {@code null}
   */
  public List<DTO> findByName(String name) {
    return convert(getDalService().findByName(name));
  }

  /**
   * Находит список элекментов словаря по имени, если ни одного элемента на найдено, то создаёт новый
   *
   * @param name имя
   * @return элементы словаря
   */
  public List<DTO> findOrCreate(String name) {
    return convert(getDalService().findOrCreate(name));
  }
}
