package com.mira.mvc.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * ������� ��� ������ � URL
 */
public class UrlUtils {
  /**
   * ������� �� ������ ��� �������, ������� �� �������� ������, ������, ���� ��� ��������������
   *
   * @param source �������� ������
   * @return ��������������� ������
   */
  public static String normalize(String source) {
    if (source == null) return null;

    return source.toLowerCase().replaceAll("[^a-zA-Z�-��-�_ 0-9]", "").replaceAll("\\s+", "_").replaceAll("_+", "_").replaceAll("^_+", "").replaceAll("_+$", "");
  }

  /**
   * �������� ������ � ����������� � ��������� url. ������� ����������� ���� / �� ����������.
   *
   * @param source �������� ������
   * @return ������������ ������
   */
  public static String encode(String source) {
    try {
      String[] parts = source.split("/", -1);
      for (int i = 0; i < parts.length; i++) {
        parts[i] = URLEncoder.encode(parts[i], "UTF-8");
      }
      return StringUtils.join(parts, "/");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  public static String get(String url, Map<String, String> parameters) throws IOException {
    HttpClient httpClient = HttpClients.createDefault();
    HttpGet get = new HttpGet(buildUri(url, parameters));
    HttpResponse httpResponse = httpClient.execute(get);
    return EntityUtils.toString(httpResponse.getEntity());
  }

  public static String buildUri(String url, Map<String, String> parameters) {
    URIBuilder uriBuilder = null;
    try {
      uriBuilder = new URIBuilder(url);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
    for (Map.Entry<String, String> entry : parameters.entrySet()) {
      uriBuilder.addParameter(entry.getKey(), entry.getValue());
    }
    return uriBuilder.toString();
  }

  public static String toRelative(String absoluteUrl) throws URISyntaxException {
    URIBuilder uriBuilder = new URIBuilder(absoluteUrl);
    return uriBuilder.getPath();
  }


}
