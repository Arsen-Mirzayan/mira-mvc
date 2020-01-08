package com.mira.mvc.validation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Список ошибок валидации. Содержит полезные методы для оперирования со списом ошибок, в том числе поиск списка ошибок
 * по полю, группировка сообщений и т.д.
 */
public class Errors {
  private List<Error> errors = new LinkedList<>();
  private List<Error> global = new LinkedList<>();
  private List<Error> alerts = new LinkedList<>();
  private Map<String, List<Error>> fieldErrors = new HashMap<>();

  public Errors() {

  }

  public Errors(List<Error> errors) {
    errors.forEach(this::add);
  }

  /**
   * Закрывает коллекцию ошибок для добавления новых элементов. Сортирует ошибки по спискам. После вызова данного метода
   *
   * @return себя же для последовательного вызова
   */
  public Errors close() {
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
    return Collections.unmodifiableList(global);
  }

  /**
   * @return список предупреждений, ошибок с типом {@link Placement#ALERT}
   */
  public List<Error> getAlerts() {
    return Collections.unmodifiableList(alerts);
  }

  /**
   * @return список всех ошибок
   */
  public List<Error> getErrors() {
    return Collections.unmodifiableList(errors);
  }

  /**
   * Добавляет новую ошибку в списки. После добавления объект ошибки нельзя изменять.
   *
   * @param error ошибка
   * @return себя же для последовательного вызова
   */
  public Errors add(Error error) {
    errors.add(error);
    switch (error.getPlacement()) {

      case GLOBAL:
        global.add(error);
        break;
      case FIELD:
        fieldErrors.computeIfAbsent(error.getField(), k -> new LinkedList<>()).add(error);
        break;
      case ALERT:
        alerts.add(error);
        break;
    }
    return this;
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
