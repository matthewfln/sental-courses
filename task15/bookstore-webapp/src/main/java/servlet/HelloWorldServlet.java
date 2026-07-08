package servlet;
import util.JpaUtil;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/hello")
public class HelloWorldServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            JpaUtil.getEntityManagerFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Тип контента и кодировку
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        // Строка "Hello world" в ответ
        try (PrintWriter writer = response.getWriter()) {
            writer.print("Hello world");
        }
    }
}
