package com.mira.mvc.service.impl;

import com.mira.jpa2.PageResponse;
import com.mira.jpa2.dao.AbstractDao;
import com.mira.jpa2.data.AbstractPersistentObject;
import com.mira.mvc.dto.AbstractEntityDto;
import com.mira.mvc.service.AbstractService;
import com.mira.mvc.service.ResourceNotFoundException;
import com.mira.mvc.validation.Errors;
import com.mira.mvc.validation.ValidationException;
import com.mira.mvc.validation.ValidationService;
import com.mira.utils.ClassUtils;
import org.dozer.Mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

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
  protected final ValidationService validationService;
  protected boolean isRestful = false;

  protected final SERVICE dalService;

  {
    ignoredProperties.add("id");
  }

  public AbstractServiceImpl(Mapper mapper, ValidationService validationService, SERVICE dalService) {
    this.mapper = mapper;
    this.validationService = validationService;
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
   * Проеобразуем объект к указанному классу с помощью маппера
   *
   * @param source    исходный объект
   * @param destClass класс, к которому нужно привести объект
   * @param <T> класс
   * @return объект указанного класса
   */
  protected <T> T convert(Object source, Class<T> destClass) {
    return source == null ? null : mapper.map(source, destClass);
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
  public void delete(DTO object) {
    validate(errors -> validateBeforeDelete(object, errors));
    getDalService().delete(toEntity(object));
  }

  /**
   * Проверяет объект перед удалением. Ошибки записываются в параметр {@code errors}. Данный метод вызывается в методе
   * {@link #delete(AbstractEntityDto)} (AbstractEntityDto)} перед собственно удалением объекта.
   *
   * @param object удаляемый объект
   * @param errors список ошибок
   */
  private void validateBeforeDelete(DTO object, Errors errors) {

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

  /**
   * Валидатор, передаваемый в параметре {@code validator}, выполняет проверку, заполняя переданный объект {@link Errors}.
   * Если по итогу список ошибок не пустой, то метод данный объект финализирует и выбрасывает {@link com.mira.mvc.validation.ValidationException}
   *
   * @param validator валидатор
   * @throws ValidationException если валидатор нашёл ошибки
   */
  protected void validate(Consumer<Errors> validator) throws ValidationException {
    Errors errors = new Errors();
    validator.accept(errors);
    validationService.throwIfNotEmpty(errors);
  }

  @Override
  public DTO save(DTO object) {
    validate(errors -> validateBeforeSave(object, errors));
    ENTITY entity = convert(object);
    getDalService().save(entity);
    return convert(entity);
  }

  /**
   * Проверяет объект перед сохранением. Ошибки записываются в параметр {@code errors}. Данный метод вызывается в методе
   * {@link #save(AbstractEntityDto)} перед собственно сохранением объекта.
   *
   * @param object сохраняемый объект
   * @param errors список ошибок
   */
  protected void validateBeforeSave(DTO object, Errors errors) {
    validationService.validate(object, errors);
  }

}
