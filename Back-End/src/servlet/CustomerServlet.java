package servlet;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.annotation.Resource;
import javax.json.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try(Connection connection = ((BasicDataSource) getServletContext().getAttribute("dbcp")).getConnection()){
            PreparedStatement pstm = connection.prepareStatement("select * from Customer");
            ResultSet rst = pstm.executeQuery();
            JsonArrayBuilder array = Json.createArrayBuilder();

            while (rst.next()) {
                JsonObjectBuilder object = Json.createObjectBuilder();
                object.add("id", rst.getString("id"));
                object.add("name", rst.getString("name"));
                object.add("address", rst.getString("address"));
                object.add("salary", rst.getDouble("salary"));
                array.add(object.build());
            }

            resp.getWriter().print(array.build());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String address = req.getParameter("address");
        String salary = req.getParameter("salary");

        try(Connection connection = ((BasicDataSource) getServletContext().getAttribute("dbcp")).getConnection()){

            PreparedStatement pstm1 = connection.prepareStatement("insert into Customer values (?,?,?,?)");
            pstm1.setString(1, id);
            pstm1.setString(2, name);
            pstm1.setString(3, address);
            pstm1.setDouble(4, Double.parseDouble(salary));

            boolean success = pstm1.executeUpdate() > 0;
            if (success) {
                JsonObjectBuilder jsonObject = Json.createObjectBuilder();
                jsonObject.add("status","done");
                jsonObject.add("message","successful...!");
                resp.getWriter().print(jsonObject.build());
            }
        } catch (SQLException e) {
            JsonObjectBuilder jsonObject = Json.createObjectBuilder();
            jsonObject.add("status","error");
            jsonObject.add("message",e.getMessage());
            resp.getWriter().print(jsonObject.build());
            resp.setStatus(404);

        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        try(Connection connection = ((BasicDataSource) getServletContext().getAttribute("dbcp")).getConnection()){

            PreparedStatement pstm2 = connection.prepareStatement("delete from Customer where id = ?");
            pstm2.setString(1, id);
            boolean success = pstm2.executeUpdate() > 0;
            JsonObjectBuilder jsonObject = Json.createObjectBuilder();

            if (success) {
                jsonObject.add("status","done");
                jsonObject.add("message","Delete successful...!");
            }else {
                jsonObject.add("status","error");
                jsonObject.add("message","No Such Customer To Delete...!");
                resp.setStatus(400);
            }
            resp.getWriter().print(jsonObject.build());

        } catch (SQLException e) {
            JsonObjectBuilder jsonObject = Json.createObjectBuilder();
            jsonObject.add("status","error");
            jsonObject.add("message",e.getMessage());
            resp.getWriter().print(jsonObject.build());
            resp.setStatus(400);

        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JsonReader reader = Json.createReader(req.getReader());
        JsonObject object = reader.readObject();

        String id = object.getString("id");
        String name = object.getString("name");
        String address = object.getString("address");
        String salary = object.getString("salary");

        try(Connection connection = ((BasicDataSource) getServletContext().getAttribute("dbcp")).getConnection()){

            PreparedStatement pstm3 = connection.prepareStatement("update Customer set name=?,address=?,salary=? where id=?");

            pstm3.setString(4, id);
            pstm3.setString(1, name);
            pstm3.setString(2, address);
            pstm3.setDouble(3, Double.parseDouble(salary));

            boolean success = pstm3.executeUpdate() > 0;
            JsonObjectBuilder jsonObject = Json.createObjectBuilder();

            if (success) {
                jsonObject.add("status","done");
                jsonObject.add("message","Update successful...!");
            }else {
                jsonObject.add("status","error");
                jsonObject.add("message","No Such Customer To Delete...!");
            }
            resp.getWriter().print(jsonObject.build());

        } catch (SQLException e) {
            JsonObjectBuilder jsonObject = Json.createObjectBuilder();
            jsonObject.add("status","error");
            jsonObject.add("message",e.getMessage());
            resp.getWriter().print(jsonObject.build());
            resp.setStatus(400);

        }
    }

}
