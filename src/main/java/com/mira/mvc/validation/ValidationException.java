package com.mira.mvc.validation;

/**
 * Ошибка валидации. Полный список ошибок содержится в {@link #getErrors()}
 */
public class ValidationException extends RuntimeException {
  private Errors errors;

  public ValidationException(Errors errors) {
    this.errors = errors;
  }

  /**
   * @return полный список ошибок валидации
   */
  public Errors getErrors() {
    return errors;
  }
}
