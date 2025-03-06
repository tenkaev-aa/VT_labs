package builders;

public abstract class Builder<T> {
  /**
   * Устанавливает имя объекта.
   *
   * @param name имя объекта.
   * @return текущий строитель.
   */
  public abstract Builder<T> setName(String name);

  /**
   * Устанавливает числовое значение объекта.
   *
   * @param value числовое значение.
   * @return текущий строитель.
   */
  public abstract Builder<T> setNumber(Number value);

  /**
   * Устанавливает перечисление (enum) объекта.
   *
   * @param value значение перечисления.
   * @return текущий строитель.
   */
  public abstract Builder<T> setEnum(Enum<?> value);

  /**
   * Устанавливает вложенный объект.
   *
   * @param value вложенный объект.
   * @return текущий строитель.
   */
  public abstract Builder<T> setNestedObject(Object value);

  /**
   * Создает и возвращает объект.
   *
   * @return созданный объект.
   */
  public abstract T build();
}
