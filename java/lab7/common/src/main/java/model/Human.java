package model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;

/**
 * Класс, представляющий человека (губернатора).
 *
 * <p>Объект этого класса хранит информацию о росте человека. Рост должен быть больше 0. Для
 */
@Embeddable
public class Human implements Serializable {

  @Serial private static final long serialVersionUID = 3L;

  @Column(name = "governor_height")
  private Float height;

  /**
   * Создает новый объект человека с заданным ростом.
   *
   * @param height рост человека. Должен быть больше 0.
   */
  public Human(Float height) {
    this.height = height;
  }

  /**
   * Возвращает значение роста человека.
   *
   * @return рост человека. Всегда больше 0.
   */
  public float getHeight() {
    return height;
  }

  public void setHeight(Float height) {
    this.height = height;
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
