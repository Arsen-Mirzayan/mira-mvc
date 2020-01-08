package com.mira.mvc.validation;


import com.mira.utils.DateUtils;
import org.junit.Before;
import org.junit.Test;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ValidationServiceTest {
  private ValidationService service;
  private MessageInterpolator messageInterpolator;

  @Before
  public void setUp() throws Exception {
    messageInterpolator = mock(MessageInterpolator.class);
    when(messageInterpolator.interpolate(anyString(), any(MessageInterpolator.Context.class))).then(invocation -> invocation.getArgument(0));
    when(messageInterpolator.interpolate(anyString(), any(MessageInterpolator.Context.class), any(Locale.class))).then(invocation -> invocation.getArgument(0));
    when(messageInterpolator.interpolate(anyString(), anyMap())).then(invocation -> invocation.getArgument(0));
    service = new ValidationService(messageInterpolator);
  }

  @Test
  public void validate() {
    Errors errors = service.validate(new Outer("", "", "\t", DateUtils.addDays(new Date(), -1), new Inner(), null, 5));
    assertTrue(errors.hasErrorForField("notEmpty"));
    assertTrue(errors.hasErrorForField("notBlank"));
    assertTrue(errors.hasErrorForField("from10to100"));

    errors = service.validate(new Outer("a", "", "b", DateUtils.addDays(new Date(), 1), new Inner(), null, 115));
    assertTrue(errors.hasErrorForField("notNullPast"));
    assertTrue(errors.hasErrorForField("from10to100"));

    errors = service.validate(new Outer("a", "", "b", null, new Inner(), "1asdasd", 15));
    assertTrue(errors.hasErrorForField("notNullPast"));
    assertTrue(errors.hasErrorForField("email"));

    errors = service.validate(new Outer("a", "", "b", DateUtils.addDays(new Date(), -1), new Inner(), "1@mail.ru", 15));
    assertTrue(errors.isEmpty());

    errors = service.validate(new Outer("a", "", "b", DateUtils.addDays(new Date(), -1), new Inner(), "1@огрн.онлайн", 15));
    assertTrue(errors.isEmpty());
  }

  /**
   * Внутренний класс для тестирования
   */
  public static class Inner {
    @NotNull
    private String notNull;

    public String getNotNull() {
      return notNull;
    }

    public void setNotNull(String notNull) {
      this.notNull = notNull;
    }
  }

  /**
   * Класс объекта для тестирования
   */
  public static class Outer {
    @DecimalMin(value = "10")
    @DecimalMax(value = "100")
    private int from10to100;
    @NotEmpty
    private String notEmpty;
    @NotNull
    private String notNull;
    @NotBlank
    private String notBlank;
    @NotNull
    @Past
    private Date notNullPast;
    @NotNull
    private Inner inner;
    @Email
    private String email;

    public Outer(String notEmpty, String notNull, String notBlank, Date notNullPast, Inner inner, String email, int from10to100) {
      this.notEmpty = notEmpty;
      this.notNull = notNull;
      this.notBlank = notBlank;
      this.notNullPast = notNullPast;
      this.inner = inner;
      this.email = email;
      this.from10to100 = from10to100;
    }

    public int getFrom10to100() {
      return from10to100;
    }

    public void setFrom10to100(int from10to100) {
      this.from10to100 = from10to100;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getNotEmpty() {
      return notEmpty;
    }

    public void setNotEmpty(String notEmpty) {
      this.notEmpty = notEmpty;
    }

    public String getNotNull() {
      return notNull;
    }

    public void setNotNull(String notNull) {
      this.notNull = notNull;
    }

    public String getNotBlank() {
      return notBlank;
    }

    public void setNotBlank(String notBlank) {
      this.notBlank = notBlank;
    }

    public Date getNotNullPast() {
      return notNullPast;
    }

    public void setNotNullPast(Date notNullPast) {
      this.notNullPast = notNullPast;
    }

    public Inner getInner() {
      return inner;
    }

    public void setInner(Inner inner) {
      this.inner = inner;
    }
  }
}