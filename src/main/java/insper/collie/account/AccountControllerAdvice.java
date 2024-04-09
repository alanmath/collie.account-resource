package insper.collie.account;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import insper.collie.account.exceptions.AccountNotFoundException;
import insper.collie.account.exceptions.EmailAlreadyExistsException;

@ControllerAdvice
public class AccountControllerAdvice extends ResponseEntityExceptionHandler{

    @ExceptionHandler({EmailAlreadyExistsException.class})
    private ResponseEntity<String> badRequestHandler(RuntimeException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler({AccountNotFoundException.class})
    private ResponseEntity<String> notFoundHandler(RuntimeException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

}