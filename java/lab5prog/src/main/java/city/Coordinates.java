package city;

import validation.validationService;

/**
 * Класс, представляющий координаты города.
 *
 * <p>Объект этого класса хранит координаты города в виде двух значений: - {@code x} — координата по
 * оси X (тип {@code double}). - {@code y} — координата по оси Y (тип {@code int}).
 *
 * <p>При создании объекта координат используется сервис {@link validationService} для валидации
 * значения координаты {@code x}. Значение {@code y} может быть любым.
 *
 * @see validationService
 */
public record Coordinates(double x, int y) {
  /**
   * Создает новый объект координат с заданными значениями {@code x} и {@code y}.
   *
   * <p>Значение координаты {@code x} проходит валидацию. Если значение не проходит валидацию,
   * выбрасывается исключение.
   *
   * @param x координата по оси X.
   * @param y координата по оси Y.
   * @throws exceptions.ValidationException если значение {@code x} не проходит валидацию.
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
  @Override
  public double x() {
    return x;
  }

  /**
   * Возвращает значение координаты по оси Y.
   *
   * @return координата по оси Y.
   */
  @Override
  public int y() {
    return y;
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
