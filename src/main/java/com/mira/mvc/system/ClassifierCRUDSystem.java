package com.mira.mvc.system;

import com.mira.jpa2.Orders;
import com.mira.jpa2.PageRequest;
import com.mira.jpa2.PageResponse;
import com.mira.jpa2.data.Classifier;
import com.mira.jpa2.data.Classifier_;
import com.mira.jpa2.service.ClassifierService;
import com.mira.mvc.dto.ClassifierDto;

/**
 * Родительский класс для систем работы с классификаторами
 *
 * @param <ENTITY>  класс сущности
 * @param <SERVICE> класс сервиса для работы с сущностью
 * @param <DTO>     класс DTO
 */
public abstract class ClassifierCRUDSystem<ENTITY extends Classifier, SERVICE extends ClassifierService<ENTITY>, DTO extends ClassifierDto> extends DefaultCRUDSystem<ENTITY, SERVICE, DTO> {
  /**
   * Находит элемент классификатора по коду
   *
   * @param code код
   * @return элемент классификатора
   */
  public DTO findByCode(String code) {
    return convert(getDalService().findByCode(code));
  }

  /**
   * Поиск по коду и по имени
   *
   * @param code     код
   * @param name     имя
   * @param page     номер страницы
   * @param pageSize размер страницы
   * @return страница с результатом
   */
  public PageResponse<DTO> search(String code, String name, long page, long pageSize) {
    PageRequest<ENTITY> request = new PageRequest<>(page, pageSize, new Orders<>(Classifier_.code));
    return convert(getDalService().search(code, name, request));
  }

  /**
   * Находит элемент классификатора по коду, если не находит,то создаёт новый
   *
   * @param code код
   * @param name имя
   * @return элемент классификатора
   */
  public DTO findOrCreate(String code, String name) {
    return convert(getDalService().findOrCreate(code, name));
  }

}
