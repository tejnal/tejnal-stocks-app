package com.tejnal.stockexchange.util;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class Utils {
  public static String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");

    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7, headerAuth.length());
    }

    return null;
  }
}
