package com.mira.mvc.service.impl;

import com.mira.jpa2.dao.AbstractDao;
import com.mira.mvc.dto.AbstractEntityDto;
import com.mira.jpa2.PageResponse;
import com.mira.jpa2.data.AbstractPersistentObject;
import com.mira.mvc.service.AbstractService;
import com.mira.mvc.service.ResourceNotFoundException;
import com.mira.utils.ClassUtils;
import com.mira.utils.DateUtils;
import org.dozer.Mapper;

import java.util.*;

/**
 * Родительский класс для всех систем, содержит общие методы.
 */
public abstract class AbstractServiceImpl<ENTITY extends AbstractPersistentObject<EntityIdClass>
    , SERVICE extends AbstractDao<ENTITY, EntityIdClass>
    , DTO extends AbstractEntityDto<DtoIdClass>
    , EntityIdClass
    , DtoIdClass>
    implements AbstractService<ENTITY, SERVICE, DTO, EntityIdClass, DtoIdClass> {
  protected final Set<String> ignoredProperties = new HashSet<>();
  protected final Mapper mapper;

  protected boolean isRestful = false;

  protected final SERVICE dalService;

  {
    ignoredProperties.add("id");
  }

  public AbstractServiceImpl(Mapper mapper, SERVICE dalService) {
    this.mapper = mapper;
    this.dalService = dalService;
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
  protected <T> T convert(ENTITY entity, Class<T> destClass) {
    return entity == null ? null : mapper.map(entity, destClass);
  }

  /**
   * Преобразует сущность к DTO объекту
   *
   * @param entity сущность
   * @return DTO
   */
  protected DTO convert(ENTITY entity) {
    return convert(entity, getDtoClass());
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
