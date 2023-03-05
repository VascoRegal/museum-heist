package MuseumHeist.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
    @Override
    public String format(LogRecord logRecord) {
      return String.format("Level: %s, %s, message: %s",
          logRecord.getLevel(), "[ " + Thread.currentThread().getName() + " ]", logRecord.getMessage());
    }
}
