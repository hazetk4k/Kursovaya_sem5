package Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MakeSql {
    public static final String USER_NAME = "root";
    public static final String PASSWORD = "1234";
    public static final String URL = "jdbc:mysql://localhost:3306/mysql";
    public static final String SELECT_QUERY = "SELECT * FROM client_auth WHERE login=";
    public static final String DELETE_PRODUCT_QUERY = "DELETE FROM products WHERE id = ?";
    public static final String SELECT_MODEL_QUERY = "SELECT * FROM ";
    public static final String SELECT_ALL_PRODUCTS = "SELECT * FROM products";
    public static final String INSERT_PRODUCT_QUERRY = "INSERT INTO products (model_name, fuel, battery, carcase, wheels) VALUES (?, ?, ?, ?, ?)";
    public static final String INSERT_QUERY = "INSERT INTO client_auth (login, password, ac_level) VALUES (?, ?, ?)";
    public static final String UPDATE_QUERY = "UPDATE products SET model_name = ?, fuel = ?, battery = ?, carcase = ?, wheels = ? WHERE id = ?";
    public static Statement statement;
    public static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    static {
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void disconnect() throws SQLException {
        statement.close();
        connection.close();
    }

    public void editProduct(String model_name, String fuel, String battery, String carcase, String wheels, String id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY)) {
            preparedStatement.setString(1, model_name);
            preparedStatement.setString(2, fuel);
            preparedStatement.setString(3, battery);
            preparedStatement.setString(4, carcase);
            preparedStatement.setString(5, wheels);
            preparedStatement.setInt(6, Integer.parseInt(id));

            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
            try {
                preparedStatement.close();
            } catch (SQLException ignored) {
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    public void deleteProduct(int id) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PRODUCT_QUERY)) {
            preparedStatement.setInt(1, id);

            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
            try {
                preparedStatement.close();
            } catch (SQLException ignored) {
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    public String getAllProducts() throws SQLException {
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_PRODUCTS);
        String result = "";
        while (resultSet.next()) {
            for (int i = 1; i <= 6; i++) {
                result += resultSet.getString(i);
                result += "; ";
            }
            result += "///";
        }
        if (result.equals("")) {
            result = "";
        }
        resultSet.close();
        return result;
    }

    public static void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }

    public String modelGetName(String table_name) throws SQLException {
        ResultSet resultSet = statement.executeQuery(SELECT_MODEL_QUERY + table_name);
        String result = "";
        while (resultSet.next()) {
            result += resultSet.getString(1);
            result += "; ";
        }
        resultSet.close();
        return result;
    }

    public String Authorization(String login, String password) throws SQLException {
        ResultSet resultSet = statement.executeQuery(SELECT_QUERY + "'" + login + "'");
        String result = "";
        while (resultSet.next()) {
            if (Objects.equals(resultSet.getString(3), password)) {
                result = resultSet.getString(4);
            }
        }
        resultSet.close();
        if (result.equals("")) {
            result = "";
        }
        return result;
    }

    public void insertNewProduct(String model_name, String fuel, String battery, String carcase, String wheels) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PRODUCT_QUERRY)) {
            preparedStatement.setString(1, model_name);
            preparedStatement.setString(2, fuel);
            preparedStatement.setString(3, battery);
            preparedStatement.setString(4, carcase);
            preparedStatement.setString(5, wheels);

            preparedStatement.executeUpdate();
            try {
                preparedStatement.close();
            } catch (SQLException ignored) {
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    public String getProductFields(String table, String condition, String param, int flag) throws SQLException {
        ResultSet resultSet = null;
        int newParam = 0;
        if (flag == 1) {
            newParam = Integer.parseInt(param);
            resultSet = statement.executeQuery(SELECT_MODEL_QUERY + table + condition + newParam);
        } else {
            resultSet = statement.executeQuery(SELECT_MODEL_QUERY + table + condition + param);
        }
        String result = "";
        if (table == "products"){
            while (resultSet.next()) {
                for (int i = 1; i <= 6; i++) {
                    result += resultSet.getString(i);
                    result += "; ";
                }
            }
        } else {
            while (resultSet.next()) {
                for (int i = 1; i <= 5; i++) {
                    result += resultSet.getString(i);
                    result += "; ";
                }
            }
        }
        if (result.equals("")) {
            result = "";
        }
        resultSet.close();
        return result;
    }

    public void insertNewClient(String login, String password, String ac_level) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, ac_level);

            preparedStatement.executeUpdate();
            try {
                preparedStatement.close();
            } catch (SQLException ignored) {
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
    }


}
