package com.mira.mvc.system;

import com.mira.jpa2.data.DictionaryObject;
import com.mira.jpa2.service.DictionaryService;
import com.mira.mvc.dto.DictionaryDto;

import java.util.List;

/**
 * Система работы со словарями
 *
 * @param <ENTITY>  тип словаря
 * @param <SERVICE> сервис для рабоыт со словарём
 * @param <DTO>     транспортный класс
 */
public interface DictionaryCRUDSystem<ENTITY extends DictionaryObject, SERVICE extends DictionaryService<ENTITY>, DTO extends DictionaryDto>
    extends DefaultCRUDSystem<ENTITY, SERVICE, DTO> {
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
