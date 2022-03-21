package com.tejnal.stockexchange.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GenericTestProperty {
	@Value("${server.port}")
	private int portNumber;

	@Value("${server.servlet.context-path}")
	private String servletContext;

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public String getServletContext() {
		return servletContext;
	}

	public void setServletContext(String servletContext) {
		this.servletContext = servletContext;
	}
}
