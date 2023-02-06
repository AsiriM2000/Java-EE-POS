package servlet;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/item")
public class ItemServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try(Connection connection = ((BasicDataSource) getServletContext().getAttribute("dbcp")).getConnection()){

            PreparedStatement pstm = connection.prepareStatement("select * from Item");
            ResultSet rst = pstm.executeQuery();

            JsonArrayBuilder array = Json.createArrayBuilder();
            while (rst.next()){
                JsonObjectBuilder object = Json.createObjectBuilder();
                object.add("code",rst.getString("code"));
                object.add("itemName",rst.getString("itemName"));
                object.add("price",rst.getDouble("price"));
                object.add("qty",rst.getString("qty"));
                array.add(object.build());
            }

            resp.setContentType("application/jason");
            resp.getWriter().print(array.build());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        String itemName = req.getParameter("itemName");
        String price = req.getParameter("price");
        String qty = req.getParameter("qty");

        try(Connection connection = ((BasicDataSource) getServletContext().getAttribute("dbcp")).getConnection()){

            PreparedStatement pstm1 = connection.prepareStatement("insert into Item values (?,?,?,?)");

            pstm1.setString(1,code);
            pstm1.setString(2,itemName);
            pstm1.setDouble(3, Double.parseDouble(price));
            pstm1.setString(4,qty);

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
        String code = req.getParameter("code");

        try(Connection connection = ((BasicDataSource) getServletContext().getAttribute("dbcp")).getConnection()){

            PreparedStatement pstm2 = connection.prepareStatement("delete from Item where code=?");
            pstm2.setString(1,code);
            boolean success = pstm2.executeUpdate() > 0;
            JsonObjectBuilder jsonObject = Json.createObjectBuilder();

            if (success) {
                jsonObject.add("status","done");
                jsonObject.add("message","Delete successful...!");
            }else {
                jsonObject.add("status","error");
                jsonObject.add("message","No Such Item To Delete...!");
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

        String code = object.getString("code");
        String itemName = object.getString("itemName");
        String price = object.getString("price");
        String qty = object.getString("qty");

        try(Connection connection = ((BasicDataSource) getServletContext().getAttribute("dbcp")).getConnection()){

            PreparedStatement pstm3 = connection.prepareStatement("update Item set itemName=?, price=?, qty=? where code=?");

            pstm3.setString(4,code);
            pstm3.setString(1,itemName);
            pstm3.setDouble(2, Double.parseDouble(price));
            pstm3.setString(3,qty);

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
