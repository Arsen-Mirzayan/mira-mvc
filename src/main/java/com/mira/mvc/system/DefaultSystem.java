package com.mira.mvc.system;

import com.mira.mvc.dto.AbstractEntityDto;
import com.mira.mvc.dto.EntityDto;
import com.mira.jpa2.PageResponse;
import com.mira.jpa2.data.AbstractPersistentObject;
import com.mira.jpa2.data.DefaultPersistentObject;
import com.mira.jpa2.service.AbstractService;
import com.mira.utils.ClassUtils;
import com.mira.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Родительский класс для всех систем, содержит общие методы.
 */
public abstract class DefaultSystem<ENTITY extends AbstractPersistentObject<EntityIdClass>
    , SERVICE extends AbstractService<ENTITY, EntityIdClass>
    , DTO extends AbstractEntityDto<DtoIdClass>
    , EntityIdClass
    , DtoIdClass> {
    protected final Set<String> ignoredProperties = new HashSet<>();
    protected final Date deletingDate = DateUtils.create(1700, 1, 1);
    protected final String deletingString = "-2147483648";
    @Autowired
    protected DtoConversionService dtoConversionService;
    protected boolean isRestful = false;

    protected Map<Class, AbstractService> daoServices = new HashMap<>();
    private SERVICE dalService;

    {
        ignoredProperties.add("id");
    }

    public abstract Class<ENTITY> getEntityClass();

    public abstract Class<EntityIdClass> getEntityIdClass();

    public abstract Class<DTO> getDtoClass();

    /**
     * @return сервис для работы с объектами текущего класса
     */
    protected SERVICE getDalService() {
        return dalService;
    }

    /**
     * @return список всех объектов
     */
    public List<DTO> findAll() {
        List<ENTITY> entities = getDalService().findAll();
        return convert(entities);
    }

    /**
     * Находит объект по идентификатору
     *
     * @param id идентификатор
     * @return найденный объект или null
     */
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
        return dtoConversionService.convert(entity, getDtoClass());
    }

    /**
     * Преобразует сущность к DTO объекту
     *
     * @param entities список сущностей
     * @return список полученных DTO
     */
    protected List<DTO> convert(List<ENTITY> entities) {
        return dtoConversionService.convertAll(entities, getDtoClass());
    }

    /**
     * Преобразует страницу ответа с сущности к DTO
     *
     * @param entities список сущностей
     * @return список DTO
     */
    protected PageResponse<DTO> convert(PageResponse<ENTITY> entities) {
        return dtoConversionService.convertPageResponse(entities, getDtoClass());
    }

    @Autowired
    public void setDaoServices(List<AbstractService> services) {
        for (AbstractService service : services) {
            daoServices.put(service.getEntityClass(), service);
        }
        dalService = (SERVICE) getDalService(getEntityClass());
    }

    /**
     * Выгружает из базы данных список сущностей, соответствующих указанным DTO объектам. Выгрузка происходит через DAL сервис,
     * соответствующий этой сущности. Если DAL Service найти не удалось, то метод выбрасывает {@link IllegalArgumentException}
     *
     * @param dtos        список DTO объектов
     * @param entityClass класс сущности
     * @param <ENTITY>    класс сущности
     * @param <DTO>       класс DTO объекта
     * @return найденная сущность.
     * @throws IllegalArgumentException указанному классу сущности не соотвествует DAL сервис
     */
    protected <ENTITY extends DefaultPersistentObject, DTO extends EntityDto> List<ENTITY> findEntity(List<DTO> dtos, Class<ENTITY> entityClass) {
        List<ENTITY> result = new ArrayList<>();
        for (DTO dto : dtos) {
            result.add(findEntity(dto, entityClass));
        }
        return result;
    }

    /**
     * Выгружает из базы данных сущность, соответствующую указанному DTO объекту. Выгрузка происходит через DAL сервис,
     * соответствующий этой сущности. Если DAL Service найти не удалось, то метод выбрасывает {@link IllegalArgumentException}
     *
     * @param dto         DTO объект
     * @param entityClass класс сущности
     * @param <ENTITY>    класс сущности
     * @param <DTO>       класс DTO объекта
     * @return найденная сущность.
     * @throws IllegalArgumentException указанному классу сущности не соотвествует DAL сервис
     */
    protected <ENTITY extends DefaultPersistentObject, DTO extends EntityDto> ENTITY findEntity(DTO dto, Class<ENTITY> entityClass) {
        return dto != null ? getDalService(entityClass).findById(dto.getId()) : null;
    }

    /**
     * Находит DAL сервис для указанного класса
     *
     * @param entityClass класс
     * @param <ENTITY>    класс сущности
     * @param <IdClass>   кдасс идентификатора сущности
     * @return соответствующий DAL сервис
     * @throws IllegalArgumentException указанному классу сущности не соотвествует DAL сервис
     */
    protected <ENTITY extends AbstractPersistentObject<IdClass>, IdClass> AbstractService<? extends ENTITY, IdClass> getDalService(Class<ENTITY> entityClass) {
        AbstractService<? extends ENTITY, IdClass> service = (AbstractService<? extends ENTITY, IdClass>) daoServices.get(entityClass);
        if (service == null) {
            for (Class cl : daoServices.keySet()) {
                if (cl.isAssignableFrom(entityClass)) {
                    service = (AbstractService<? extends ENTITY, IdClass>) daoServices.get(cl);
                }
            }
        }
        if (service != null) {
            return service;
        } else {
            throw new IllegalArgumentException(String.format("Can't find DAL Service for class %s", entityClass.getName()));
        }
    }

    /**
     * Удаляет сущность, соответствующую этому объекту
     *
     * @param dto объект, который нужно удалить
     */
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
        return toEntity(dto, getEntityClass(), getEntityIdClass());
    }

    /**
     * Создаёт сущность с тем же идентификатором, что и DTO
     *
     * @param dto     исходная DTO
     * @param cl      класс сущности
     * @param idClass класс идентификатора
     * @param <T>     конечный класс сущности
     * @param <ID>    класс идентификатора сущности
     * @return созданную сущность
     */
    protected <T extends AbstractPersistentObject<ID>, ID> T toEntity(AbstractEntityDto dto, Class<T> cl, Class<ID> idClass) {
        if (dto == null) {
            return null;
        } else {
            try {
                T entity = cl.newInstance();
                entity.setId(extractId(dto, idClass));
                return entity;
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private <ID> ID extractId(AbstractEntityDto dto, Class<ID> idClass) {
        if (dto instanceof EntityDto) {
            return ClassUtils.convert(idClass, dto.getId());
        }
        ID id;
        try {
            id = idClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        copy(dto.getId(), id);
        return id;
    }

    /**
     * Достаёт из базы данных объект с указанным идентификатором, либо создаёт новый, а также заплоняет все поля
     * на основе данных из dto. Не сохраняет в базу полученные данные.
     *
     * @param source источник - dto
     * @param cl     класс сущности
     * @param <T>    класс сущности
     * @return экземплар сущности
     */
    protected <T extends AbstractPersistentObject> T convertToEntity(AbstractEntityDto source, Class<T> cl) {
        try {
            T result = (T) getDalService(cl).findById(extractId(source, getEntityIdClass()));
            if (result == null) {
                result = cl.newInstance();
            }
            copy(source, result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Копирует все свойства из DTO в ENTITY. Свойства, которые сами являются хранимыми объектами, достаются из базы
     * по идентификаторам.
     *
     * @param source источник DTO
     * @param result результирующий ENTITY
     */
    protected void copy(Object source, Object result) {
        try {
            Map<String, PropertyDescriptor> sourceBeanInfo = new HashMap<>();
            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(source.getClass(), AbstractEntityDto.class).getPropertyDescriptors()) {
                if (propertyDescriptor.getWriteMethod() != null && propertyDescriptor.getReadMethod() != null) {
                    sourceBeanInfo.put(propertyDescriptor.getName(), propertyDescriptor);
                }
            }
            Map<String, PropertyDescriptor> destinationBeanInfo = new HashMap<>();
            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(result.getClass(), AbstractPersistentObject.class).getPropertyDescriptors()) {
                if (propertyDescriptor.getWriteMethod() != null && propertyDescriptor.getReadMethod() != null) {
                    destinationBeanInfo.put(propertyDescriptor.getName(), propertyDescriptor);
                }
            }

            for (String propertyName : sourceBeanInfo.keySet()) {
                if (destinationBeanInfo.containsKey(propertyName)
                    && !ignoredProperties.contains(propertyName)) {
                    Object sourceValue = sourceBeanInfo.get(propertyName).getReadMethod().invoke(source);
                    PropertyDescriptor destinationDescriptor = destinationBeanInfo.get(propertyName);
                    Class<?> destinationClass = destinationDescriptor.getPropertyType();
                    Method destinationWriteMethod = destinationDescriptor.getWriteMethod();
                    if (sourceValue == null
                        || destinationWriteMethod == null) {
                        continue;
                    }
                    Object destinationValue;
                    if (isDeleteValue(sourceValue)) { //в свойстве специальная константа, которая говорит, что надо обнулить свойство
                        destinationValue = null;
                    } else if (sourceValue instanceof EntityDto) {
                        if (DefaultPersistentObject.class.isAssignableFrom(destinationClass)) {
                            destinationValue = getDalService((Class<? extends AbstractPersistentObject>) destinationClass).findById(((EntityDto) sourceValue).getId());
                        } else {
                            continue;
                        }
                    } else if (destinationClass.isAssignableFrom(sourceValue.getClass())) { //Конвертируем простое значение
                        destinationValue = sourceValue;
                    } else {
                        continue;
                    }
                    destinationWriteMethod.invoke(result, destinationValue);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Проверяет, является ли переданное значение маркером на удаление
     *
     * @param value значение
     * @return {@code true} если это маркер
     */
    protected boolean isDeleteValue(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Date) {
            return deletingDate.compareTo((Date) value) == 0;
        } else if (value instanceof EntityDto) {
            return ((EntityDto) value).getId() < 0;
        } else {
            return deletingString.equals(value.toString());
        }
    }

    /**
     * Проверяет, является ли переданное значение маркером на удаление
     *
     * @param value значение
     * @return {@code true} если это маркер
     */
    protected boolean isDeleteValue(Collection<? extends EntityDto> value) {
        return value != null && value.size() == 1 && value.iterator().next().getId() < 0;
    }

    protected <T> PageResponse<T> emptyResponse() {
        return new PageResponse<>(Collections.<T>emptyList(), 0, 0, 0);
    }
}
