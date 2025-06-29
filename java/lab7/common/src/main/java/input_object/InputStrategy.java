package input_object;

import java.io.IOException;

public interface InputStrategy<T> {
  T inputObject() throws IOException;
}
