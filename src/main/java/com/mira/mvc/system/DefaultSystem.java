package com.mira.mvc.system;

import com.mira.jpa2.data.AbstractPersistentObject;
import com.mira.jpa2.service.AbstractService;
import com.mira.mvc.dto.AbstractEntityDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Родительский класс для всех систем, содержит общие методы. Интерфейсы системы принимают только транспортные объекты в
 * качестве параметров и возвращают транспортные объекты. Реализация данного интерфейса инкапсулирует работы с хранимыми
 * сущностями и конвертацию последних в транспортные объекты.
 */
public interface DefaultSystem<
    ENTITY extends AbstractPersistentObject<EntityIdClass>
    , SERVICE extends AbstractService<ENTITY, EntityIdClass>
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
