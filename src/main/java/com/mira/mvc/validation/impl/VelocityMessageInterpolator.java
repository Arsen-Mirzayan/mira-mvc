package com.mira.mvc.validation.impl;

import com.mira.mvc.validation.MessageInterpolator;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

/**
 * Реализация интерполятора сообщений на основе движка {@link VelocityEngine}. В конструкторе принимает список шаблонов,
 * которые кешируется движком. При вызове методов{@code interpolate} сначала ищет сохранённый скопилированный шаблон,
 * если его нет, то создаёт новый по переданному текстовому шаблону.
 */
public class VelocityMessageInterpolator implements MessageInterpolator {
  private static final String ENCODING = "UTF-8";
  private final VelocityEngine engine;

  /**
   * Создаёт новый экземпляр интерполятора. Инициализирует {@link VelocityEngine} и загружает в него переданные шаблоны.
   *
   * @param templates карта шаблонов. Ключ - имя шаблон, значение - сам шаблон.
   */
  public VelocityMessageInterpolator(Map<String, String> templates) {
    this.engine = new VelocityEngine();
    engine.setProperty(Velocity.RESOURCE_LOADER, "string");
    engine.addProperty("string.resource.loader.class", StringResourceLoader.class.getName());
    engine.addProperty("string.resource.loader.repository.static", "false");
    engine.init();

    StringResourceRepository repository = (StringResourceRepository) engine.getApplicationAttribute(StringResourceLoader.REPOSITORY_NAME_DEFAULT);
    templates.forEach((key, template) -> repository.putStringResource(key, template, ENCODING));
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
}
