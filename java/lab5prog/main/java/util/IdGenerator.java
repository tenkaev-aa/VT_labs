package util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Утилити класс для генерации уникальных идентификаторов.
 *
 * <p>Этот класс предоставляет метод для генерации уникальных целочисленных идентификаторов с
 * использованием {@link AtomicInteger}. Каждый вызов метода {@link #getAndIncrement()} возвращает
 * новое уникальное значение, начиная с 1.
 *
 * @see AtomicInteger
 */
public final class IdGenerator {
  private static final AtomicInteger counter = new AtomicInteger(1);

  /**
   * Возвращает текущее значение счетчика и увеличивает его на 1.
   *
   * <p>Метод гарантирует атомарность операции.
   *
   * @return текущее значение счетчика.
   */
  public static int getAndIncrement() {
    return counter.getAndIncrement();
  }
}
