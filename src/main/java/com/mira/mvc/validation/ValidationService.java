package com.mira.mvc.validation;

import com.mira.utils.StringUtils;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Сервис для заполнения текстов ошибок
 */
public class ValidationService {

  private final ErrorMessageHolder errorMessageHolder;

  public ValidationService(ErrorMessageHolder errorMessageHolder) {
    this.errorMessageHolder = errorMessageHolder;
  }

  /**
   * Заполняет текст сообщения ошибки в соответствии с кодом и аргументами
   *
   * @param error ошибка, в которой нужно заполнить текст
   * @return та же ошибка с заполненым текстом.
   */
  private Error fillMessage(Error error) {
    if (StringUtils.isNotEmpty(error.getCode())) {
      String message = errorMessageHolder.getMessageByCode(error.getCode());
      if (StringUtils.isNotEmpty(message) && error.getArguments() != null) {
        for (Map.Entry<String, String> entry : error.getArguments().entrySet()) {
          message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
      }
      error.setMessage(message);
    }
    return error;
  }

  /**
   * Добавляет ошибку в список при условии выполнения переданного условия
   *
   * @param errors    список ошибок. Если пустой, то будет создан новый пустой список.
   * @param condition условие добавления ошибки
   * @param error     ошибка
   * @return список ошибок
   */
  public Errors addErrorIf(Errors errors, Supplier<Boolean> condition, Error error) {
    if (errors == null) {
      errors = new Errors();
    }
    if (condition.get()) {
      errors.getErrors().add(error);
    }
    return errors;
  }

  /**
   * Добавляет ошибку в список при условии выполнения переданного условия. Ошибка создаётся с типом {@link Placement#FIELD}
   *
   * @param errors      список ошибок. Если пустой, то будет создан новый пустой список.
   * @param condition   условие добавления ошибки
   * @param field       поле, в котором произошла ошибка
   * @param messageCode код сообщения
   * @return список ошибок
   */
  public Errors addErrorIf(Errors errors, Supplier<Boolean> condition, String field, String messageCode) {
    if (errors == null) {
      errors = new Errors();
    }
    if (condition.get()) {
      errors.getErrors().add(fillMessage(new Error(Placement.FIELD, field, messageCode)));
    }
    return errors;
  }

  /**
   * Добавляет ошибку в список, если указанное значение пустое, т.е {@code null} или пустая строка
   *
   * @param errors      список ошибок. Если пустой, то будет создан новый пустой список.
   * @param value       значение, которое проверяем
   * @param field       поле, в котором произошла ошибка
   * @param messageCode код сообщения
   * @return список ошибок
   */
  public Errors addErrorIfEmpty(Errors errors, String value, String field, String messageCode) {
    return addErrorIf(errors, () -> StringUtils.isEmpty(value), field, messageCode);
  }

  /**
   * Добавляет ошибку в список, если указанное значение {@code null}
   *
   * @param errors      список ошибок. Если пустой, то будет создан новый пустой список.
   * @param value       значение, которое проверяем
   * @param field       поле, в котором произошла ошибка
   * @param messageCode код сообщения
   * @return список ошибок
   */
  public Errors addErrorIfNull(Errors errors, Object value, String field, String messageCode) {
    return addErrorIf(errors, () -> value == null, field, messageCode);
  }

  /**
   * Проверяет список ошибок и если он не пустой, то подготавливает исключение и выбрасывает его.
   *
   * @param errors список ошибок
   * @throws ValidationException если список ошибок не пустой
   */
  public void throwIfNotEmpty(Errors errors) throws ValidationException {
    if (errors != null && errors.isEmpty()) {
      errors.getErrors().forEach(this::fillMessage);
      errors.makeCache();
      throw new ValidationException(errors);
    }
  }

}
