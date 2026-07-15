package security;

import com.fasterxml.jackson.databind.ObjectMapper;
import exception.ErrorMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ErrorMessage message = new ErrorMessage("Forbidden", "У вас недостаточно прав для выполнения этой операции.");
        new ObjectMapper().writeValue(response.getOutputStream(), message);
    }
}
