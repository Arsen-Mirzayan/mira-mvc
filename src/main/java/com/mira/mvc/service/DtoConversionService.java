package com.mira.mvc.service;

import com.mira.jpa2.PageResponse;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class DtoConversionService {

  private final Map<Class<?>, Map<Class<?>, Method>> METHODS;
  @Autowired
  protected Mapper mapper;

  {
    METHODS = new HashMap<>();
    for (Method method : getClass().getMethods()) {
      if (!method.getName().equals("convert")) {
        continue;
      }

      if (method.getParameterTypes().length != 2) {
        continue;
      }
      Class sourceClass = method.getParameterTypes()[0];
      Class destinationClass = method.getParameterTypes()[1];
      Map<Class<?>, Method> sourceMap = METHODS.get(sourceClass);
      if (sourceMap == null) {
        sourceMap = new HashMap<>();
        METHODS.put(sourceClass, sourceMap);
      }
      sourceMap.put(destinationClass, method);
    }
  }

  /**
   * Производит базовую конвертацию свойств, используя описанный маппинг.
   *
   * @param source           источник
   * @param destinationClass класс объекта назначения
   * @param <T>              тип класса объекта, который мы хотим получить
   * @return объект указанного класса
   */
  protected <T> T convertBasic(Object source, Class<T> destinationClass) {
    if (source == null) {
      return null;
    }
    return mapper.map(source, destinationClass);
  }

  /**
   * Создаёт объект указанного типа и заполняет его свойства на основе источника
   *
   * @param source           источник
   * @param destinationClass класс объекта назначения
   * @param <T>              тип класса объекта, который мы хотим получить
   * @return новый объект
   */
  public <T> T convert(Object source, Class<T> destinationClass) {
    T result = convertBasic(source, destinationClass);
    if (result != null) {
      convertAdditional(source, result);
    }
    return result;
  }

  /**
   * Создаёт список объектов указанного типа и заполняет их свойства на основе источника
   *
   * @param sources          список источников
   * @param destinationClass класс объекта назначения
   * @param <T>              тип объекта, который мы хотим получить
   * @return новый объект
   */
  public <T> List<T> convertAll(List sources, Class<T> destinationClass) {
    List<T> result = new ArrayList<>(sources.size());
    for (Object source : sources) {
      result.add(convert(source, destinationClass));
    }
    return result;
  }

  /**
   * Создаёт список объектов указанного типа и заполняет их свойства на основе источника
   *
   * @param sources          список источников
   * @param destinationClass класс объекта назначения
   * @param <T>              тип объекта, который мы хотим получить
   * @return новый объект
   */
  public <T> Set<T> convertAll(Set sources, Class<T> destinationClass) {
    Set<T> result = new HashSet<>(sources.size());
    for (Object source : sources) {
      result.add(convert(source, destinationClass));
    }
    return result;
  }

  /**
   * Конвертирует данные в результате запроса
   *
   * @param source           исходный результат запроса
   * @param destinationClass класс назначения
   * @param <T>              тип объекта, который мы хотим получить
   * @return новый результат запроса
   */
  public <T> PageResponse<T> convertPageResponse(PageResponse source, Class<T> destinationClass) {
    return new PageResponse<>(convertAll(source.getResult(), destinationClass), source.getPage(), source.getPageCount(), source.getRecordCount());
  }

  /**
   * Ищет метод в текущем классе, который отвечает за дополнительную конвертацию объектов. Если метод найден,
   * то вызывает его. Если метод не найден, то ничего не происходит.
   *
   * @param source источник
   * @param result частично заполненный результат
   * @param <T>    тип объекта, который мы хотим получить
   */
  protected <T> void convertAdditional(Object source, T result) {
    Class<?> sourceClass = source.getClass();
    while (sourceClass != null) {
      List<Class> classes = new LinkedList<>();
      classes.add(sourceClass);
      Collections.addAll(classes, sourceClass.getInterfaces());
      for (Class cl : classes) {
        Map<Class<?>, Method> methodMap = METHODS.get(cl);
        if (methodMap != null) {
          Class<?> resultClass = result.getClass();
          while (resultClass != null) {
            Method conversionMethod = methodMap.get(resultClass);
            if (conversionMethod != null) {
              try {
                conversionMethod.invoke(this, source, result);
              } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
              }
            }
            resultClass = resultClass.getSuperclass();
          }
        }
      }
      sourceClass = sourceClass.getSuperclass();
    }
  }}

