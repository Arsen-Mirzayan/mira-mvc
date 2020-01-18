package com.mira.mvc.validation;

import com.mira.mvc.validation.impl.VelocityMessageInterpolator;
import org.junit.Before;
import org.junit.Test;

import javax.validation.MessageInterpolator;
import javax.validation.metadata.ConstraintDescriptor;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VelocityMessageInterpolatorTest {
  private VelocityMessageInterpolator interpolator;
  private MessageSource messageSource;
  private Map<String, String> canonicalKeys;

  @Before
  public void setUp() throws Exception {
    messageSource = mock(MessageSource.class);
    canonicalKeys = new HashMap<>();
    interpolator = new VelocityMessageInterpolator(messageSource, canonicalKeys);
  }

  @Test
  public void canonical() {
    canonicalKeys.put("notCanonical", "canonical");
    assertEquals("canonical", interpolator.getCanonicalKey("notCanonical"));
    assertEquals("some other key", interpolator.getCanonicalKey("some other key"));
  }

  @Test
  public void interpolate() {
    //Настраиваем шаблоны
    when(messageSource.getMessage(eq("common.message"))).thenReturn("Привет $name.");
    when(messageSource.getMessage(eq("greater.or.equals"))).thenReturn("значение должно быть больше #if($include)или равно #end$value.");
    when(messageSource.getMessage(eq("Пока $name"))).thenReturn(null);

    //Настраиваем контекст ошибки
    ConstraintDescriptor constraintDescriptor = mock(ConstraintDescriptor.class);
    Map<String, Object> contextValues = new HashMap<>();
    when(constraintDescriptor.getAttributes()).thenReturn(contextValues);
    MessageInterpolator.Context context = mock(MessageInterpolator.Context.class);
    when(context.getConstraintDescriptor()).thenReturn(constraintDescriptor);


    //Проверяем простой шаблон
    contextValues.put("name", "Пётр");
    assertEquals("Привет Пётр.", interpolator.interpolate("common.message", context));

    //Меняем контекст
    contextValues.put("name", "Иван");
    assertEquals("Привет Иван.", interpolator.interpolate("common.message", context));

    //Проверяем отсутствующий шаблон
    assertEquals("Пока Иван", interpolator.interpolate("Пока $name", context));

    //Проверяем шаблон с if
    contextValues.clear();
    contextValues.put("include", true);
    contextValues.put("value", 10);
    assertEquals("значение должно быть больше или равно 10.", interpolator.interpolate("greater.or.equals", context));

    //Меняем контекст
    contextValues.clear();
    contextValues.put("include", false);
    contextValues.put("value", 10);
    assertEquals("значение должно быть больше 10.", interpolator.interpolate("greater.or.equals", context));
  }
}