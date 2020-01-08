package com.mira.mvc.validation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Список ошибок валидации. Содержит полезные методы для оперирования со списом ошибок, в том числе поиск списка ошибок
 * по полю, группировка сообщений и т.д.
 */
public class Errors {
  private List<Error> errors;
  private List<Error> global;
  private List<Error> alerts;
  private Map<String, List<Error>> fieldErrors;

  public Errors() {
    errors = new LinkedList<>();
  }

  public Errors(List<Error> errors) {
    this.errors = errors;
    makeCache();
  }

  /**
   * Заполняет кеш для быстрого поиска
   *
   * @return себя же для последовательного вызова
   */
  public Errors makeCache() {
    fieldErrors = new HashMap<>();
    global = errors.stream().filter(error -> Placement.GLOBAL.equals(error.getPlacement())).collect(Collectors.toList());
    alerts = errors.stream().filter(error -> Placement.ALERT.equals(error.getPlacement())).collect(Collectors.toList());
    fieldErrors = errors.stream().collect(Collectors.toMap(
        Error::getField
        , o -> errors.stream().filter(error -> o.getField().equals(error.getField())).collect(Collectors.toList())
    ));
    return this;
  }

  /**
   * @return {@code true} если список ошибок пустой
   */
  public boolean isEmpty() {
    return errors.isEmpty();
  }

  /**
   * @return список глобальных ошибок, ошибок с типом {@link Placement#GLOBAL}
   */
  public List<Error> getGlobal() {
    return global;
  }

  /**
   * @return список предупреждений, ошибок с типом {@link Placement#ALERT}
   */
  public List<Error> getAlerts() {
    return alerts;
  }

  /**
   * @return список всех ошибок
   */
  public List<Error> getErrors() {
    return errors;
  }

  /**
   * Находит список ошибок для указанного поля
   *
   * @param field имя поля, для которого ищем ошибки
   * @return список ошибок, возможно пустой, но не null
   */
  public List<Error> findByField(String field) {
    List<Error> errors = fieldErrors.get(field);
    return errors != null ? errors : new LinkedList<>();
  }

  /**
   * Проверяет, есть ли у указанного поля ошибки
   *
   * @param field поле
   * @return {@code true} если есть ошибки, иначе {@code false}
   */
  public boolean hasErrorForField(String field) {
    return !findByField(field).isEmpty();
  }

  /**
   * Собирает текст всех ошибок по указанному полю в единую строку
   *
   * @param field поле
   * @return сборный текст всех ошибок
   */
  public String findFieldMessages(String field) {
    return findFieldMessages(field, " ");
  }

  /**
   * Собирает текст всех ошибок по указанному полю в единую строку
   *
   * @param field     поле
   * @param separator разделитель для сообщений
   * @return сборный текст всех ошибок
   */
  public String findFieldMessages(String field, String separator) {
    return findByField(field).stream().map(Error::getMessage).collect(Collectors.joining(separator));
  }


  /**
   * Собирает текст всех глобальных ошибок (ошибок с типом {@link Placement#GLOBAL})
   *
   * @return сборный текст всех ошибок
   */
  public String findGlobalMessages() {
    return findGlobalMessages(" ");
  }

  /**
   * Собирает текст всех глобальных ошибок (ошибок с типом {@link Placement#GLOBAL})
   *
   * @param separator разделитель для сообщений
   * @return сборный текст всех ошибок
   */
  public String findGlobalMessages(String separator) {
    return getGlobal().stream().map(Error::getMessage).collect(Collectors.joining(separator));
  }
}
