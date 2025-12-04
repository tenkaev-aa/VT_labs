package web;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Named("clockBean")
@RequestScoped
public class ClockBean {

  private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public String getNow() {
    return LocalDateTime.now().format(FMT);
  }
}
