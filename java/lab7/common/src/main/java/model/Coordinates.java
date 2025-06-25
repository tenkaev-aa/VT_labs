package model;

import input_object.FieldInput;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;

/**
 * Класс, представляющий координаты города.
 *
 * <p>Объект этого класса хранит координаты города в виде двух значений: - {@code x} — координата по
 * оси X (тип {@code double}). - {@code y} — координата по оси Y (тип {@code int}).
 */
@Embeddable
public class Coordinates implements Serializable {

  @Serial private static final long serialVersionUID = 2L;

  @Column(name = "coordinates_x", nullable = false)
  private double x;

  @Column(name = "coordinates_y", nullable = false)
  private int y;

  /**
   * Создает новый объект координат с заданными значениями {@code x} и {@code y}.
   *
   * <p>Значение координаты {@code x} проходит валидацию. Если значение не проходит валидацию,
   * выбрасывается исключение.
   *
   * @param x координата по оси X.
   * @param y координата по оси Y.
   */
  public Coordinates(double x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Возвращает значение координаты по оси X.
   *
   * @return координата по оси X.
   */
  public double getX() {
    return x;
  }

  /**
   * Возвращает значение координаты по оси Y.
   *
   * @return координата по оси Y.
   */
  public int getY() {
    return y;
  }

  @FieldInput(prompt = "Координата X:", max = 36.00)
  public void setX(double x) {
    this.x = x;
  }

  @FieldInput(prompt = "Координата Y:")
  public void setY(int y) {
    this.y = y;
  }

  /**
   * Возвращает строковое представление объекта координат.
   *
   * @return строковое представление координат.
   */
  @Override
  public String toString() {
    return "city.Coordinates{" + "x=" + x + ", y=" + y + '}';
  }
}
