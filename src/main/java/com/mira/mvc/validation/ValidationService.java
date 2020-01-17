package com.mira.mvc.validation;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Проверяет объекты на основе {@link Validator}. Полученный результат проверки конвертируется в {@link Errors}.
 */
public class ValidationService {

  private final MessageInterpolator messageInterpolator;
  private final Validator validator;

  /**
   * Создаёт сервис проверки с указанным интерполятором сообщений
   *
   * @param messageInterpolator интерполятор сообщений для преобразование кода ошибки в читаемый текст
   */
  public ValidationService(MessageInterpolator messageInterpolator) {
    this.messageInterpolator = messageInterpolator;
    validator = Validation.byDefaultProvider().configure().messageInterpolator(messageInterpolator).buildValidatorFactory().getValidator();
  }

  /**
   * Проверяет список ошибок и если он не пустой, то интерполирует все сообщения, подготавливает исключение и выбрасывает его.
   *
   * @param errors список ошибок
   * @throws ValidationException если список ошибок не пустой
   */
  public void throwIfNotEmpty(Errors errors) throws ValidationException {
    if (errors != null && !errors.isEmpty()) {
      errors.getErrors().forEach(error -> {
        if (isNotEmpty(error.getCode()) && isEmpty(error.getMessage())) {
          String message = messageInterpolator.interpolate(error.getCode(), error.getArguments());
          error.setMessage(message);
        }
      });
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
        .forEach(errors::reject);
    return errors;
  }

}
