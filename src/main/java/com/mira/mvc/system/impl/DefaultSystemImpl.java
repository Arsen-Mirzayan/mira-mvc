package com.mira.mvc.system.impl;

import com.mira.mvc.dto.AbstractEntityDto;
import com.mira.mvc.dto.EntityDto;
import com.mira.jpa2.PageResponse;
import com.mira.jpa2.data.AbstractPersistentObject;
import com.mira.jpa2.data.DefaultPersistentObject;
import com.mira.jpa2.service.AbstractService;
import com.mira.mvc.system.DefaultSystem;
import com.mira.mvc.system.DtoConversionService;
import com.mira.mvc.system.ResourceNotFoundException;
import com.mira.utils.ClassUtils;
import com.mira.utils.DateUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Родительский класс для всех систем, содержит общие методы.
 */
public abstract class DefaultSystemImpl<ENTITY extends AbstractPersistentObject<EntityIdClass>
    , SERVICE extends AbstractService<ENTITY, EntityIdClass>
    , DTO extends AbstractEntityDto<DtoIdClass>
    , EntityIdClass
    , DtoIdClass> implements DefaultSystem<ENTITY, SERVICE, DTO, EntityIdClass, DtoIdClass> {
  protected final Set<String> ignoredProperties = new HashSet<>();
  protected final Date deletingDate = DateUtils.create(1700, 1, 1);
  protected final String deletingString = "-2147483648";
  @Autowired
  protected Mapper mapper;

  protected boolean isRestful = false;

  private SERVICE dalService;

  {
    ignoredProperties.add("id");
  }

  /**
   * @return сервис для работы с объектами текущего класса
   */
  protected SERVICE getDalService() {
    return dalService;
  }

  @Override
  public List<DTO> findAll() {
    List<ENTITY> entities = getDalService().findAll();
    return convert(entities);
  }

  @Override
  public DTO find(DtoIdClass id) {
    ENTITY entity = getDalService().findById(convertId(id));
    if (isRestful && entity == null) {
      throw new ResourceNotFoundException();
    }
    return convert(entity);
  }

  protected abstract EntityIdClass convertId(DtoIdClass dtoId);

  /**
   * Преобразует сущность к DTO объекту
   *
   * @param entity сущность
   * @return DTO
   */
  protected DTO convert(ENTITY entity) {
    return entity == null ? null : mapper.map(entity, getDtoClass());
  }

  /**
   * Преобразует транспортный объект к хранимому объекту. Пытается найти хранимые объект по идентификатору, а если не находит
   * то создаёт новый.
   * <p>
   * <b>Важно:</b> базовое преобразование игнорирует зависимоти. Зависимости должны быть установлены в дочерних классах.
   * </p>
   *
   * @param dto транспортный объект
   * @return хранимый объект
   */
  protected ENTITY convert(DTO dto) {
    ENTITY entity = null;
    EntityIdClass id = extractId(dto);
    if (id != null) {
      entity = dalService.findById(id);
    }
    if (entity == null) {
      entity = ClassUtils.newInstance(getEntityClass());
    }
    mapper.map(dto, entity);
    return entity;
  }

  /**
   * Преобразует сущность к DTO объекту
   *
   * @param entities список сущностей
   * @return список полученных DTO
   */
  protected List<DTO> convert(List<ENTITY> entities) {
    List<DTO> result = new ArrayList<>(entities.size());
    for (ENTITY source : entities) {
      result.add(convert(source));
    }
    return result;
  }

  /**
   * Преобразует страницу ответа с сущности к DTO
   *
   * @param source страница с ответом
   * @return список DTO
   */
  protected PageResponse<DTO> convert(PageResponse<ENTITY> source) {
    return new PageResponse<>(convert(source.getResult()), source.getPage(), source.getPageCount(), source.getRecordCount());
  }

  @Override
  public void delete(DTO dto) {
    getDalService().delete(Collections.singletonList(toEntity(dto)));
  }


  /**
   * Создаёт сущность с тем же идентификатором, что и DTO
   *
   * @param dto исходная DTO
   * @return созданную сущность
   */
  protected ENTITY toEntity(DTO dto) {
    return dto == null ? null : dalService.findById(extractId(dto));
  }

  protected abstract EntityIdClass extractId(DTO dto);

  @Override
  public DTO save(DTO object) {
    ENTITY entity = convert(object);
    getDalService().save(entity);
    return convert(entity);
  }

}
