package com.mira.mvc.validation;

/**
 * Получает сообщение по ключу.
 */
public interface MessageSource {
  /**
   * Возвращает текст сообщения по ключу. Если по данному ключу нет сообщений, то возвращается {@code null}
   *
   * @param key ключ
   * @return найденное сообщение или {@code null}
   */
  String getMessage(String key);
}
