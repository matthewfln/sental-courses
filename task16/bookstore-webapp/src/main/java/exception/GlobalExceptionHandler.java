package exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleIllegalArgument(IllegalArgumentException ex) {
        LOGGER.warn("Ошибка запроса (Bad Request): {}", ex.getMessage());
        return new ErrorMessage("Bad Request", ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage handleIllegalState(IllegalStateException ex) {
        LOGGER.warn("Конфликт бизнес-логики (Conflict): {}", ex.getMessage());
        return new ErrorMessage("Conflict", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleAllExceptions(Exception ex) {
        LOGGER.error("Внутренняя ошибка сервера: ", ex);
        return new ErrorMessage("Internal Server Error", "Произошла непредвиденная ошибка на сервере.");
    }
}
