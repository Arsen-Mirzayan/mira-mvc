package com.mira.mvc.validation;

import java.util.HashMap;
import java.util.Map;

/**
 * Информация о единичной ошибке. Содержит полную информацию об ошибке для её корректной обработке на стороне клиента.<br>
 * {@link #placement} - место, в котором возникла ошибка.<br>
 * {@link #field} - поле, в котором возникла ошибка. Обязательно, если {@code placement} равно {@link Placement#FIELD}.<br>
 * {@link #code} - код ошибки, который клиент может обработать самостоятельно.<br>
 * {@link #message} - текст ошибки, соответствующий коду ошибки и языку, определённому автоматически.<br>
 * {@link #arguments} - список параметров для ошибки. Например, если код ошибки имеет текст {@code дата должна быть не менее minDate},
 * то карта параметров должна содержать параметр {@code minDate}.<br>
 */
public class Error {
  private Placement placement;
  private String field;
  private String code;
  private String message;
  private Map<String, String> arguments = new HashMap<>();

  public Error() {
  }

  public Error(Placement placement, String field, String code) {
    this.placement = placement;
    this.field = field;
    this.code = code;
  }

  public Placement getPlacement() {
    return placement;
  }

  public void setPlacement(Placement placement) {
    this.placement = placement;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Map<String, String> getArguments() {
    return arguments;
  }

  public void setArguments(Map<String, String> arguments) {
    this.arguments = arguments;
  }
}
