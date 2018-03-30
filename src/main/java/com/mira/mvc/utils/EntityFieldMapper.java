
package com.mira.mvc.utils;

import com.mira.mvc.dto.EntityDto;
import com.mira.jpa2.data.DefaultPersistentObject;
import com.mira.utils.ClassUtils;
import org.dozer.CustomFieldMapper;
import org.dozer.classmap.ClassMap;
import org.dozer.fieldmap.FieldMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EntityFieldMapper implements CustomFieldMapper {
  @Override
  public boolean mapField(Object source, Object destination, Object sourceFieldValue, ClassMap classMap, FieldMap fieldMapping) {
    if (sourceFieldValue instanceof DefaultPersistentObject) {
      String destFieldName = fieldMapping.getDestFieldName();
      Method setter = ClassUtils.getSetter(destination.getClass(), destFieldName);
      Class<?> destFieldType = setter.getParameterTypes()[0];
      if (EntityDto.class.isAssignableFrom(destFieldType)) {
        Long id = ((DefaultPersistentObject) sourceFieldValue).getId();
        EntityDto dto = (EntityDto) ClassUtils.newInstance(destFieldType);
        dto.setId(id);
        try {
          setter.invoke(destination, dto);
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new RuntimeException(e);
        }
      }
      return true;
    }
    return false;
  }
}
