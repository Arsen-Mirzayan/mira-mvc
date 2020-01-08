package com.mira.mvc.validation;

import com.mira.mvc.validation.impl.VelocityMessageInterpolator;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.MessageInterpolator;
import javax.validation.metadata.ConstraintDescriptor;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VelocityMessageInterpolatorTest {

  @Test
  public void interpolate() {
    //Настраиваем шаблоны
    Map<String, String> templates = new HashMap<>();
    templates.put("common.message", "Привет $name.");
    templates.put("greater.or.equals", "значение должно быть больше #if($include)или равно #end$value.");

    //Настраиваем контекст ошбки
    ConstraintDescriptor constraintDescriptor = mock(ConstraintDescriptor.class);
    Map<String, Object> contextValues = new HashMap<>();
    when(constraintDescriptor.getAttributes()).thenReturn(contextValues);
    MessageInterpolator.Context context = mock(MessageInterpolator.Context.class);
    when(context.getConstraintDescriptor()).thenReturn(constraintDescriptor);

    VelocityMessageInterpolator interpolator = new VelocityMessageInterpolator(templates);

    //Проверяем простой шаблон
    contextValues.put("name", "Пётр");
    Assert.assertEquals("Привет Пётр.", interpolator.interpolate("common.message", context));

    //Меняем контекст
    contextValues.put("name", "Иван");
    Assert.assertEquals("Привет Иван.", interpolator.interpolate("common.message", context));

    //Проверяем отсутствующий шаблон
    Assert.assertEquals("Пока Иван", interpolator.interpolate("Пока $name", context));

    //Проверяем шаблон с if
    contextValues.clear();
    contextValues.put("include", true);
    contextValues.put("value", 10);
    Assert.assertEquals("значение должно быть больше или равно 10.", interpolator.interpolate("greater.or.equals", context));

    //Меняем контекст
    contextValues.clear();
    contextValues.put("include", false);
    contextValues.put("value", 10);
    Assert.assertEquals("значение должно быть больше 10.", interpolator.interpolate("greater.or.equals", context));
  }
}