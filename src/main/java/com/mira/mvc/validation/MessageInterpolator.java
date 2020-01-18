package com.mira.mvc.validation;

import java.util.Map;

/**
 * <p>
 * Расширение интерфейса {@link javax.validation.MessageInterpolator}, чтобы можно было составлять текст сообщения
 * на основе карты аргуметов.
 * </p>
 * <p>
 * Также определяет такое понятие, как каноничный ключ. Ключи для разных валидационных механизмов может быть разные и
 * инструмент каноничных ключей позволяет их привести к елиному формату для внешних источников.
 * </p>
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


  /**
   * Возращает каноничное значение для ключа.
   *
   * @param key ключ
   * @return каноничный ключ
   */
  default String getCanonicalKey(String key) {
    return key;
  }
}
