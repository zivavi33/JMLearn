package net.acimon.jmlearn.utils;

import java.util.logging.*;

/**
 * A utility class for logging messages at different severity levels.
 * <p>
 * This class provides a simple logging mechanism using Java's built-in logging framework.
 * It can log messages with different levels such as INFO, WARNING, and SEVERE. You can add a console handler 
 * to direct logs to the console, and you can set the logging level to control the verbosity of logs.
 * </p>
 *
 * <h2>Usage Example</h2>
 * <pre>
    ConsoleHandler consoleHandler = new ConsoleHandler();
    consoleHandler.setLevel(Level.ALL);  // Set the level to log all levels
    logger.addHandler(consoleHandler);

    logger.setLevel(Level.ALL); // This can be adjusted to control the level of logging
    
    logMessage("This is an INFO message", Level.INFO);
    logMessage("This is a WARNING message", Level.WARNING);
    logMessage("This is an ERROR message", Level.SEVERE);
    logMessage("This is a CRITICAL message", Level.SEVERE);  
 * </pre>
 * <p>
 * The logMessage method can be called to log a message at the specified severity level (INFO, WARNING, or SEVERE).
 * The severity levels correspond to different logging behaviors, where:
 * - INFO messages are general information about the program’s state.
 * - WARNING messages indicate a potential issue that doesn't stop the program.
 * - SEVERE messages indicate critical issues that may require attention.
 * </p>
 * 
 * @see Logger for more advanced logging features.
 */


public class LogHandler {
    private static final Logger logger = Logger.getLogger(LogHandler.class.getName());

    public static void logMessage(String message, Level level) {
        if (level.equals(Level.INFO)) {
            logger.info(message);  // Will print to stdout by default
        } else if (level.equals(Level.WARNING)) {
            logger.warning(message);  // Will print to stdout
        } else if (level.equals(Level.SEVERE)) {
            logger.severe(message);  // Will print to stderr
        }
    }
}
