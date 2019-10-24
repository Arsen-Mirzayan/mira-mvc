package com.mira.mvc.system;

import com.mira.jpa2.PageResponse;
import com.mira.jpa2.data.Classifier;
import com.mira.jpa2.service.ClassifierService;
import com.mira.mvc.dto.ClassifierDto;

/**
 * Родительский класс для систем работы с классификаторами
 *
 * @param <ENTITY>  класс сущности
 * @param <SERVICE> класс сервиса для работы с сущностью
 * @param <DTO>     класс DTO
 */
public interface ClassifierCRUDSystem<ENTITY extends Classifier, SERVICE extends ClassifierService<ENTITY>, DTO extends ClassifierDto>
    extends DictionaryCRUDSystem<ENTITY, SERVICE, DTO> {
  /**
   * Находит элемент классификатора по коду
   *
   * @param code код
   * @return элемент классификатора
   */
  DTO findByCode(String code);

  /**
   * Поиск по коду и по имени
   *
   * @param code     код
   * @param name     имя
   * @param page     номер страницы
   * @param pageSize размер страницы
   * @return страница с результатом
   */
  PageResponse<DTO> search(String code, String name, long page, long pageSize);

  /**
   * Находит элемент классификатора по коду, если не находит,то создаёт новый
   *
   * @param code код
   * @param name имя
   * @return элемент классификатора
   */
  DTO findOrCreate(String code, String name);
}
