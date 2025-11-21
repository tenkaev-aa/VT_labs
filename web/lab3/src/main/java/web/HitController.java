package web;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import repo.HitResultRepository;
import service.HitTester;
import model.HitResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("hitBean")
@SessionScoped
public class HitController implements Serializable {

  // входные поля формы
  private Integer x;     // selectOneMenu {-4..4}
  private String yText;  // inputText (-5..5)
  private Double r = 1.0; // p:spinner (0.1..3)

  private List<HitResult> results = new ArrayList<>();
  private Double lastExecMs;

  @Inject
  private HitResultRepository repo;

  @PostConstruct
  public void init() {
    results = repo.findAllDesc(100);
  }

  // action с формы
  public String submit() {
      try {
    double y = parseY(yText);
    long t0 = System.nanoTime();
    boolean hit = HitTester.isHit(x, y, r);
    long execmicros = (System.nanoTime() - t0) / 1_000;
    lastExecMs = execmicros / 1000.0;

    HitResult saved = repo.save(new HitResult(x, y, r, hit, execmicros));
    results.add(0, saved);
  } catch (IllegalArgumentException e) {
        jakarta.faces.context.FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Некорректное значение Y", e.getMessage()));
    }return null;
  }


  private static double parseY(String s) {
    if (s == null) throw new IllegalArgumentException("Y empty");
    String t = s.trim().replace(',', '.');
    double v = Double.parseDouble(t);
    if (v < -5 || v > 5) throw new IllegalArgumentException("Y out of range");
    return v;
  }

  public Integer getX() { return x; }
  public void setX(Integer x) { this.x = x; }
  public String getYText() { return yText; }
  public void setYText(String yText) { this.yText = yText; }
  public Double getR() { return r; }
  public void setR(Double r) { this.r = r; }
  public List<HitResult> getResults() { return results; }
  public Double getLastExecMs() { return lastExecMs; }
}
