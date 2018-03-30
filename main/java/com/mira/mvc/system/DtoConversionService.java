/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  com.mira.jpa2.PageResponse
 *  org.dozer.Mapper
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.mira.mvc.system;

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
   * ���������� ������� ����������� �������, ��������� ��������� �������.
   *
   * @param source           ��������
   * @param destinationClass ����� ������� ����������
   * @param <T>              ��� ������ �������, ������� �� ����� ��������
   * @return ������ ���������� ������
   */
  protected <T> T convertBasic(Object source, Class<T> destinationClass) {
    if (source == null) {
      return null;
    }
    return mapper.map(source, destinationClass);
  }

  /**
   * ������ ������ ���������� ���� � ��������� ��� �������� �� ������ ���������
   *
   * @param source           ��������
   * @param destinationClass ����� ������� ����������
   * @param <T>              ��� ������ �������, ������� �� ����� ��������
   * @return ����� ������
   */
  public <T> T convert(Object source, Class<T> destinationClass) {
    T result = convertBasic(source, destinationClass);
    if (result != null) {
      convertAdditional(source, result);
    }
    return result;
  }

  /**
   * ������ ������ �������� ���������� ���� � ��������� �� �������� �� ������ ���������
   *
   * @param sources          ������ ����������
   * @param destinationClass ����� ������� ����������
   * @param <T>              ��� �������, ������� �� ����� ��������
   * @return ����� ������
   */
  public <T> List<T> convertAll(List sources, Class<T> destinationClass) {
    List<T> result = new ArrayList<>(sources.size());
    for (Object source : sources) {
      result.add(convert(source, destinationClass));
    }
    return result;
  }

  /**
   * ������ ������ �������� ���������� ���� � ��������� �� �������� �� ������ ���������
   *
   * @param sources          ������ ����������
   * @param destinationClass ����� ������� ����������
   * @param <T>              ��� �������, ������� �� ����� ��������
   * @return ����� ������
   */
  public <T> Set<T> convertAll(Set sources, Class<T> destinationClass) {
    Set<T> result = new HashSet<>(sources.size());
    for (Object source : sources) {
      result.add(convert(source, destinationClass));
    }
    return result;
  }

  /**
   * ������������ ������ � ���������� �������
   *
   * @param source           �������� ��������� �������
   * @param destinationClass ����� ����������
   * @param <T>              ��� �������, ������� �� ����� ��������
   * @return ����� ��������� �������
   */
  public <T> PageResponse<T> convertPageResponse(PageResponse source, Class<T> destinationClass) {
    return new PageResponse<>(convertAll(source.getResult(), destinationClass), source.getPage(), source.getPageCount(), source.getRecordCount());
  }

  /**
   * ���� ����� � ������� ������, ������� �������� �� �������������� ����������� ��������. ���� ����� ������,
   * �� �������� ���. ���� ����� �� ������, �� ������ �� ����������.
   *
   * @param source ��������
   * @param result �������� ����������� ���������
   * @param <T>    ��� �������, ������� �� ����� ��������
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

