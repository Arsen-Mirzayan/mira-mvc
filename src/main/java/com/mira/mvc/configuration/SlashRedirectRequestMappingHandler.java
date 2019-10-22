package com.mira.mvc.configuration;

import com.mira.utils.UrlUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.lang.reflect.Method;

public class SlashRedirectRequestMappingHandler extends RequestMappingHandlerMapping {
  @Override
  protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
    super.registerHandlerMethod(handler, method, mapping);
    mapping.getPatternsCondition().getPatterns().stream()
        .filter(pattern -> !pattern.equalsIgnoreCase("/") && !pattern.isEmpty() && pattern.endsWith("/"))
        .forEach(pattern -> registerRedirect(pattern, mapping));
  }

  private void registerRedirect(String originalPattern, RequestMappingInfo mapping) {
    String newPattern = originalPattern.substring(0, originalPattern.length() - 1);

    registerHandlerMethod(new RedirectHandler(originalPattern)
        , redirectMethod
        , copyMappingInfo(mapping, newPattern));
  }

  private RequestMappingInfo copyMappingInfo(RequestMappingInfo existing, String url) {
    String[] patterns = resolveEmbeddedValuesInPatterns(new String[]{url});

    PatternsRequestCondition patternsMatcher = new PatternsRequestCondition(
        patterns, getUrlPathHelper(), getPathMatcher(),
        useSuffixPatternMatch(), false, getFileExtensions()
    );

    return new RequestMappingInfo(
        patternsMatcher,
        existing.getMethodsCondition(),
        existing.getParamsCondition(),
        existing.getHeadersCondition(),
        existing.getConsumesCondition(),
        existing.getProducesCondition(),
        existing.getCustomCondition()
    );
  }

  private static final Method redirectMethod;

  static {
    try {
      redirectMethod = RedirectHandler.class.getMethod("redirect");
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  private static class RedirectHandler {
    private String redirectUrl;

    private RedirectHandler(String redirectUrl) {
      this.redirectUrl = redirectUrl;
    }

    public View redirect() {
      RedirectView view = new RedirectView(UrlUtils.encode(redirectUrl).replace("%7B", "{").replace("%7D", "}"));
      view.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
      return view;
    }
  }
}
