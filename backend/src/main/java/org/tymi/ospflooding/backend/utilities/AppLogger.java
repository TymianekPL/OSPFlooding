package org.tymi.ospflooding.backend.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppLogger {
     static final int INDENT_SIZE = 5;

     public enum Level {
          TRACE, DEBUG, INFO, WARN, ERROR
     }
     public enum Category {
          CONTROLLER, SERVICE, REPOSITORY, HTTP, DATABASE, CRON
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

     private String format(Level level, String message, int indent) {
          String colour = switch (level) {
               case TRACE -> MAGENTA;
               case DEBUG -> BLUE;
               case INFO -> GREEN;
               case WARN -> YELLOW;
               case ERROR -> RED;
          };
          String indentPrefix = " ".repeat(indent * INDENT_SIZE);
          return String.format("%s[%s][%s]%s %s%s", colour, level, category, indentPrefix, message, RESET);
     }

     private void log(Level level, String message, int indent) {
          String formatted = format(level, message, indent);
          switch (level) {
               case TRACE -> logger.trace(formatted);
               case DEBUG -> logger.debug(formatted);
               case INFO -> logger.info(formatted);
               case WARN -> logger.warn(formatted);
               case ERROR -> logger.error(formatted);
          }
     }

     public Diagnostic trace(String msg) { return logWithDiagnostics(Level.TRACE, msg); }
     public Diagnostic debug(String msg) { return logWithDiagnostics(Level.DEBUG, msg); }
     public Diagnostic info(String msg) { return logWithDiagnostics(Level.INFO, msg); }
     public Diagnostic warn(String msg) { return logWithDiagnostics(Level.WARN, msg); }
     public Diagnostic error(String msg) { return logWithDiagnostics(Level.ERROR, msg); }

     private Diagnostic logWithDiagnostics(Level level, String message) {
          log(level, message, 0);
          return new Diagnostic(level, 1);
     }

     public class Diagnostic {
          private final Level baseLevel;
          private final int indent;

          private Diagnostic(Level baseLevel, int indent) {
               this.baseLevel = baseLevel;
               this.indent = indent;
          }

          public Diagnostic sub(Level level, String msg) {
               log(level, msg, indent);
               return new Diagnostic(baseLevel, indent + 1);
          }

          public Diagnostic trace(String msg) { return sub(Level.TRACE, msg); }
          public Diagnostic debug(String msg) { return sub(Level.DEBUG, msg); }
          public Diagnostic info(String msg) { return sub(Level.INFO, msg); }
          public Diagnostic warn(String msg) { return sub(Level.WARN, msg); }
          public Diagnostic error(String msg) { return sub(Level.ERROR, msg); }
     }
}
