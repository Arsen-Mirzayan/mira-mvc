package com.mira.mvc.validation;

import com.mira.utils.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Сервис для заполнения текстов ошибок
 */
public class ValidationService {

  private final MessageInterpolator messageInterpolator;
  private final Validator validator;

  public ValidationService(MessageInterpolator messageInterpolator) {
    this.messageInterpolator = messageInterpolator;
    validator = Validation.byDefaultProvider().configure().messageInterpolator(messageInterpolator).buildValidatorFactory().getValidator();
  }

  /**
   * Добавляет ошибку в список при условии выполнения переданного условия
   *
   * @param errors    список ошибок. Если пустой, то будет создан новый пустой список.
   * @param condition условие добавления ошибки
   * @param error     ошибка
   * @return список ошибок
   */
  public Errors rejectIf(Errors errors, Supplier<Boolean> condition, Error error) {
    if (errors == null) {
      errors = new Errors();
    }
    if (condition.get()) {
      add(errors, error);
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
  public Errors rejectIf(Errors errors, Supplier<Boolean> condition, String field, String messageCode) {
    if (errors == null) {
      errors = new Errors();
    }
    if (condition.get()) {
      add(errors, new Error(Placement.FIELD, field, messageCode));
    }
    return errors;
  }

  /**
   * Добавляет ошибку в список, предварительно её подготовив, а именно заполнив текст ошибки на основе кода/шаблона
   * и аргуметов.
   *
   * @param errors список ошибок
   * @param error  добавляемая ошибка
   */
  private void add(Errors errors, Error error) {
    if (StringUtils.isNotEmpty(error.getCode())) {
      String message = messageInterpolator.interpolate(error.getCode(), error.getArguments());
      error.setMessage(message);
    }
    errors.add(error);
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
  public Errors rejectIfEmpty(Errors errors, String value, String field, String messageCode) {
    return rejectIf(errors, () -> StringUtils.isEmpty(value), field, messageCode);
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
  public Errors rejectIfNull(Errors errors, Object value, String field, String messageCode) {
    return rejectIf(errors, () -> value == null, field, messageCode);
  }

  /**
   * Проверяет список ошибок и если он не пустой, то подготавливает исключение и выбрасывает его.
   *
   * @param errors список ошибок
   * @throws ValidationException если список ошибок не пустой
   */
  public void throwIfNotEmpty(Errors errors) throws ValidationException {
    if (errors != null && !errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  /**
   * Выполянет проверку через {@link Validator} и возвращает список
   *
   * @param bean проверяемый объект
   * @return список ошибок
   */
  public Errors validate(Object bean) {
    return validate(bean, new Errors());
  }

  /**
   * Выполянет проверку через {@link Validator} и добавляет список ошибок в переданный список
   *
   * @param bean   проверяемый объект
   * @param errors список ошибок
   * @return переданный список ошибок с дополнениями
   */
  public Errors validate(Object bean, Errors errors) {
    Set<ConstraintViolation<Object>> violations = validator.validate(bean);
    violations.stream()
        .map(violation ->
            new Error(Placement.FIELD
                , StreamSupport.stream(violation.getPropertyPath().spliterator(), false).map(Path.Node::getName).collect(Collectors.joining("."))
                , violation.getMessageTemplate()
            ))
        .forEach(error -> add(errors, error));
    return errors;
  }

}
