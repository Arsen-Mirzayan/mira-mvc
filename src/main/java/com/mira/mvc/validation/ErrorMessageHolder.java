package com.mira.mvc.validation;

/**
 * Интерфейс для получения текста сообщения об ошибке по коду
 */
public interface ErrorMessageHolder {
  /**
   * Получает текст сообщения по коду. Если по коду ничего не найдено, то возвращает {@code null}
   *
   * @param code код сообщения
   * @return текст сообщения
   */
  String getMessageByCode(String code);
}
