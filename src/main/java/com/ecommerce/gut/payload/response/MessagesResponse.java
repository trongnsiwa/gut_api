package com.ecommerce.gut.payload.response;

import java.util.Map;

public class MessagesResponse {
  
  private Map<String, String> messages;

  public MessagesResponse(Map<String, String> messages) {
    this.messages = messages;
  }

  public Map<String,String> getMessages() {
    return this.messages;
  }

  public void setMessages(Map<String,String> messages) {
    this.messages = messages;
  }
  
}
