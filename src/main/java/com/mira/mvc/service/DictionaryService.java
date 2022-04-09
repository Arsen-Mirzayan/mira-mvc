package com.mira.mvc.service;

import com.mira.jpa2.dao.DictionaryDao;
import com.mira.jpa2.data.DictionaryObject;
import com.mira.mvc.dto.DictionaryDto;

import java.util.List;

/**
 * Система работы со словарями
 *
 * @param <ENTITY>  тип словаря
 * @param <DTO>     транспортный класс
 */
public interface DictionaryService<ENTITY extends DictionaryObject, DTO extends DictionaryDto>
    extends DefaultService<ENTITY, DTO> {
  /**
   * Находит элемент справочника по имени
   *
   * @param name имя элемента справочника
   * @return найденные элементы или {@code null}
   */
  List<DTO> findByName(String name);

  /**
   * Находит список элекментов словаря по имени, если ни одного элемента на найдено, то создаёт новый
   *
   * @param name имя
   * @return элементы словаря
   */
  List<DTO> findOrCreate(String name);
}
