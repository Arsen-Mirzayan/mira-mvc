package com.mira.mvc.validation.impl;

import com.mira.mvc.validation.MessageInterpolator;
import com.mira.mvc.validation.MessageSource;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResource;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 * Реализация интерполятора сообщений на основе движка {@link VelocityEngine}. В конструкторе принимает список шаблонов,
 * которые кешируется движком. При вызове методов{@code interpolate} сначала ищет сохранённый скопилированный шаблон,
 * если его нет, то создаёт новый по переданному текстовому шаблону.
 * </p>
 * <p>
 * Шаблоны загружаются левниво из MessageSource при первом обращении. После этого шаблон может быть кеширован и изменение
 * шаблона в {@link MessageSource} не приведёт к изменению скомпилированного шаблона.
 * </p>
 */
public class VelocityMessageInterpolator implements MessageInterpolator {
  private static final String ENCODING = "UTF-8";
  private final VelocityEngine engine;
  private final Map<String, String> canonicalKeys;

  /**
   * Создаёт новый экземпляр интерполятора. Инициализирует {@link VelocityEngine}, в который шаблоны загружаются из
   * {@link MessageSource}.
   *
   * @param messageSource источник шаблонов сообщений
   */
  public VelocityMessageInterpolator(MessageSource messageSource) {
    this(messageSource, Collections.emptyMap());
  }

  /**
   * Создаёт новый экземпляр интерполятора. Инициализирует {@link VelocityEngine}, в который шаблоны загружаются из
   * {@link MessageSource}.
   *
   * @param messageSource источник шаблонов сообщений
   */
  public VelocityMessageInterpolator(MessageSource messageSource, Map<String, String> canonicalKeys) {
    this.canonicalKeys = canonicalKeys != null ? canonicalKeys : Collections.emptyMap();
    this.engine = new VelocityEngine();
    engine.setProperty(Velocity.RESOURCE_LOADER, "string");
    engine.addProperty("string.resource.loader.class", StringResourceLoader.class.getName());
    engine.addProperty("string.resource.loader.repository.static", "false");
    engine.addProperty("string.resource.loader." + StringResourceLoader.REPOSITORY_CLASS, DynamicStringResourceRepository.class.getName());
    engine.init();

    DynamicStringResourceRepository repository = (DynamicStringResourceRepository) engine.getApplicationAttribute(StringResourceLoader.REPOSITORY_NAME_DEFAULT);
    repository.setMessageSource(messageSource);
  }

  @Override
  public String interpolate(String messageTemplate, Context context) {
    return interpolate(messageTemplate, context.getConstraintDescriptor().getAttributes());
  }

  @Override
  public String interpolate(String messageTemplate, Context context, Locale locale) {
    return interpolate(messageTemplate, context);
  }

  @Override
  public String interpolate(String messageTemplate, Map<String, Object> arguments) {
    VelocityContext velocityContext = new VelocityContext();
    arguments.forEach(velocityContext::put);

    StringWriter writer = new StringWriter();
    if (engine.resourceExists(messageTemplate)) {
      engine.getTemplate(messageTemplate).merge(velocityContext, writer);
    } else {
      engine.evaluate(velocityContext, writer, messageTemplate, messageTemplate);
    }
    return writer.toString();
  }

  @Override
  public String getCanonicalKey(String key) {
    return canonicalKeys.getOrDefault(key, key);
  }

  /**
   * Репозиторий, который получает сообщения из внешнего источника {@link MessageSource}
   */
  public static class DynamicStringResourceRepository implements StringResourceRepository {
    private MessageSource messageSource;

    public MessageSource getMessageSource() {
      return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
      this.messageSource = messageSource;
    }

    @Override
    public StringResource getStringResource(String name) {
      String message = messageSource != null ? messageSource.getMessage(name) : null;
      return message != null ? new StringResource(message, getEncoding()) : null;
    }

    @Override
    public void putStringResource(String name, String body) {
      //do noting
    }

    @Override
    public void putStringResource(String name, String body, String encoding) {
      //do noting
    }

    @Override
    public void removeStringResource(String name) {
      //do noting
    }

    @Override
    public void setEncoding(String encoding) {
      //do noting
    }

    @Override
    public String getEncoding() {
      return ENCODING;
    }
  }

}
