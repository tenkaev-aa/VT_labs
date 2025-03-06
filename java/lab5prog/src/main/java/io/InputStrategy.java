package io;

import java.util.Scanner;

public interface InputStrategy<T> {
  T inputObject(Scanner scanner);
}
