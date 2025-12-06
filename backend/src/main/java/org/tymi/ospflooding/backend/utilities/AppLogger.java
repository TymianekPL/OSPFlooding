package org.tymi.ospflooding.backend.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppLogger {
     public enum Level {
          TRACE, DEBUG, INFO, WARN, ERROR
     }
     public enum Category {
          CONTROLLER, SERVICE, REPOSITORY, HTTP, DATABASE
     }

     private final Logger logger;
     private final Category category;

     private static final String RESET = "\u001B[0m";
     private static final String RED = "\u001B[31m";
     private static final String GREEN = "\u001B[32m";
     private static final String YELLOW = "\u001B[33m";
     private static final String BLUE = "\u001B[34m";
     private static final String MAGENTA = "\u001B[35m";
     private static final String CYAN = "\u001B[36m";

     public AppLogger(Class<?> clazz, Category category) {
          this.logger = LoggerFactory.getLogger(clazz);
          this.category = category;
     }

     private String format(Level level, String message) {
          String colour;
          switch (level) {
               case TRACE -> colour = MAGENTA;
               case DEBUG -> colour = BLUE;
               case INFO -> colour = GREEN;
               case WARN -> colour = YELLOW;
               case ERROR -> colour = RED;
               default -> colour = RESET;
          }
          return String.format("%s[%s][%s] %s%s", colour, level, category, message, RESET);
     }

     public void log(Level level, String message) {
          switch (level) {
               case TRACE -> logger.trace(format(level, message));
               case DEBUG -> logger.debug(format(level, message));
               case INFO -> logger.info(format(level, message));
               case WARN -> logger.warn(format(level, message));
               case ERROR -> logger.error(format(level, message));
          }
     }

     public void trace(String msg) { log(Level.TRACE, msg); }
     public void debug(String msg) { log(Level.DEBUG, msg); }
     public void info(String msg) { log(Level.INFO, msg); }
     public void warn(String msg) { log(Level.WARN, msg); }
     public void error(String msg) { log(Level.ERROR, msg); }
}
