package io;

import java.io.IOException;
import java.util.Scanner;

public interface InputStrategy<T> {
  T inputObject(Scanner scanner) throws IOException;
}
