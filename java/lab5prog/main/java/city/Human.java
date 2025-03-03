package city;

import validation.validationService;

/**
 * Класс, представляющий человека (губернатора).
 *
 * <p>Объект этого класса хранит информацию о росте человека. Рост должен быть больше 0. Для
 * валидации значения роста используется сервис {@link validationService}.
 *
 * @see validationService
 */
public class Human {
  private final Float height; // Значение поля должно быть больше 0

  /**
   * Создает новый объект человека с заданным ростом.
   *
   * <p>Значение роста валидируется с помощью {@link validationService#validateHeight(Float)}. Если
   * значение не проходит валидацию, выбрасывается исключение.
   *
   * @param height рост человека. Должен быть больше 0.
   * @throws exceptions.ValidationException если значение роста не проходит валидацию.
   */
  public Human(Float height) {
    this.height = validationService.validateHeight(height);
  }

  /**
   * Возвращает значение роста человека.
   *
   * @return рост человека. Всегда больше 0.
   */
  public float getHeight() {
    return height;
  }

  /**
   * Возвращает строковое представление объекта человека.
   *
   * @return строковое представление человека.
   */
  @Override
  public String toString() {
    return "city.Human{" + "height=" + height + '}';
  }
}
