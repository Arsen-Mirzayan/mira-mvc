package com.mira.mvc.service;

import com.mira.jpa2.PageResponse;
import com.mira.jpa2.dao.TreeClassifierDao;
import com.mira.jpa2.data.TreeClassifier;
import com.mira.mvc.dto.TreeClassifierDto;

import java.util.List;

/**
 * Система для работы с классификаторами
 */
public interface TreeClassifierService<ENTITY extends TreeClassifier<ENTITY>, DTO extends TreeClassifierDto>
    extends ClassifierService<ENTITY, DTO> {
  /**
   * Находит список дочерних элементов
   *
   * @param parent родительский элемент
   * @return список дочерних элементов
   */
  List<DTO> findChildren(DTO parent);

  List<DTO> findRoot();

  /**
   * Находит список дочерних категорий. Если параметр parent пустой, то возвращает список родительских категорий
   *
   * @param parent   родительская категория
   * @param page     номер страницы
   * @param pageSize размер страницы
   * @return список дочерних категорий
   */
  PageResponse<DTO> findChildren(DTO parent, long page, long pageSize);
}
