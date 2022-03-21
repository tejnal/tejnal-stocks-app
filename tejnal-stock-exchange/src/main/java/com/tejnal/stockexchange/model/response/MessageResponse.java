package com.tejnal.stockexchange.model.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"status", "description"})
public class MessageResponse {
  private String status;
  private String description;

  public MessageResponse(String status, String description) {
    this.status = status;
    this.description = description;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
