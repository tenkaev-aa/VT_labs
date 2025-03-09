package commands;

import java.io.BufferedReader;

public interface ScriptAwareCommand {
  void setScriptMode(boolean isScriptMode, BufferedReader scriptReader);
}
