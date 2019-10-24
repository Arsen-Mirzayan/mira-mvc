package com.mira.mvc.service;

import com.mira.jpa2.dao.AbstractDao;
import com.mira.jpa2.data.AbstractPersistentObject;
import com.mira.mvc.dto.AbstractEntityDto;

import java.util.List;

/**
 * Родительский класс для всех систем, содержит общие методы. Интерфейсы системы принимают только транспортные объекты в
 * качестве параметров и возвращают транспортные объекты. Реализация данного интерфейса инкапсулирует работы с хранимыми
 * сущностями и конвертацию последних в транспортные объекты.
 */
public interface DefaultService<
    ENTITY extends AbstractPersistentObject<EntityIdClass>
    , SERVICE extends AbstractDao<ENTITY, EntityIdClass>
    , DTO extends AbstractEntityDto<DtoIdClass>, EntityIdClass, DtoIdClass
    > {
  Class<ENTITY> getEntityClass();

  Class<EntityIdClass> getEntityIdClass();

  Class<DTO> getDtoClass();

  /**
   * @return список всех объектов
   */
  List<DTO> findAll();

  /**
   * Находит объект по идентификатору
   *
   * @param id идентификатор
   * @return найденный объект или null
   */
  DTO find(DtoIdClass id);

  /**
   * Удаляет сущность, соответствующую этому объекту
   *
   * @param dto объект, который нужно удалить
   */
  void delete(DTO dto);

  /**
   * Находит хранимый объект, соответствующий указанному DTO, и обновляет его свойства. Либо сохраняет новый.
   *
   * @param object DTO объект
   * @return сохранённый объект
   */
  DTO save(DTO object);
}
