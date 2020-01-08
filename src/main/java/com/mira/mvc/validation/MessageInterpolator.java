package com.mira.mvc.validation;

import java.util.Map;

/**
 * Расширение интерфейса {@link javax.validation.MessageInterpolator}, чтобы можно было составлять текст сообщения
 * на основе карты аргуметов
 */
public interface MessageInterpolator extends javax.validation.MessageInterpolator {

  /**
   * Составляет текст сообщения на основе шаблона и параметров
   *
   * @param messageTemplate шаблон
   * @param arguments       параметры
   * @return составленный текст
   */
  String interpolate(String messageTemplate, Map<String, Object> arguments);
}
