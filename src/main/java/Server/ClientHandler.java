package Server;

import Server.Interfaces.Connected;
import Server.Interfaces.Connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

class ClientHandler implements Runnable, Connected {
    private final List<Connection> connections;


    Thread thread;

    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.thread = new Thread(this, "Client thread");
        this.thread.start();
        this.clientSocket = socket;
        this.connections = new LinkedList<>();

    }

    public String countQuality(String[] baseFields, String[] productFields) {
        String result = "";
        for (int j = 0; j < 4; j++) {
            String[] thisBaseField = baseFields[j].split("; ");
            String[] thisProductField = productFields[j + 1].split("; ");
            for (int i = 1; i < 4; i++) {
                float count = Float.parseFloat(thisProductField[i]) / Float.parseFloat(thisBaseField[i]);
                String res = new DecimalFormat("#0.00").format(count);
                result += String.valueOf(res);
                result += "; ";
            }
            result += "///";
        }
        return result;
    }

    public String getProductValues(MakeSql makeSql, String id) throws SQLException {
        String product_fields = "";
        product_fields = makeSql.getProductFields("products", " WHERE id = ", id, 1);
        String[] fields_arr = product_fields.split("; ");
        product_fields += "///";
        product_fields += makeSql.getProductFields("table_fuel", " WHERE model_name = ", "'" + fields_arr[2] + "'", 0);
        product_fields += "///";
        product_fields += makeSql.getProductFields("table_battery", " WHERE model_name = ", "'" + fields_arr[3] + "'", 0);
        product_fields += "///";
        product_fields += makeSql.getProductFields("table_carcase", " WHERE model_name = ", "'" + fields_arr[4] + "'", 0);
        product_fields += "///";
        product_fields += makeSql.getProductFields("table_wheels", " WHERE model_name = ", "'" + fields_arr[5] + "'", 0);
        product_fields += "///";
        return product_fields;
    }

    public String getBaseValues(MakeSql makeSql) throws SQLException {
        String baseValues = "";
        baseValues += makeSql.getProductFields("table_fuel", " WHERE model_name = ", "'BaseValue'", 0);
        baseValues += "///";
        baseValues += makeSql.getProductFields("table_battery", " WHERE model_name = ", "'BaseValue'", 0);
        baseValues += "///";
        baseValues += makeSql.getProductFields("table_carcase", " WHERE model_name = ", "'BaseValue'", 0);
        baseValues += "///";
        baseValues += makeSql.getProductFields("table_wheels", " WHERE model_name = ", "'BaseValue'", 0);
        baseValues += "///";
        return baseValues;
    }

    public String DiffMethod(String quality) {
        String result = "";
        List<Integer> neg = new ArrayList<>();
        boolean isFullEqual = true;
        boolean isFullNegative = true;
        boolean isFullPositive = true;
        boolean isThereNeg = false;
        String[] eachQuality = quality.split("; ");
        for (int i = 0; i < 3; i++) {
            if (!Objects.equals(eachQuality[i], "1,00")) {
                isFullEqual = false;
            }
            if (Float.parseFloat(eachQuality[i]) < 1) {
                neg.add(i);
                isFullPositive = false;
                isThereNeg = true;
            }
            if (Float.parseFloat(eachQuality[i]) > 1) {
                isFullNegative = false;
            }
        }
        if (isFullEqual) {
            return "Уровень качества равен базовому по всем показателям";
        } else {
            if (isFullNegative) {
                return "Уровень качества ниже базового по всем показателям";
            }
            if (isFullPositive) {
                return "Уровень качества выше базового по всем показателям";
            }
            if (isThereNeg) {
                result = "Уровень качества ниже базового у следующих показателей: ";
                for (int args : neg) {
                    result += args + "| ";
                }
            } else {
                return "Уровень качества каждого показателя выше бозового или равен ему";
            }
        }
        return result;
    }

    @Override
    public void run() {
        notifyConnections("Подключен клиент: ip: " + clientSocket.getInetAddress().getHostAddress() + ", порт: " + clientSocket.getPort(), 1);
        PrintWriter out;
        BufferedReader in;
        boolean flag = true;
        while (flag) {
            try {
                out = new PrintWriter(this.clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                MakeSql makeSql = new MakeSql();
                String line = in.readLine();
                String result = "";
                switch (line) {
                    case "Disconnect":
                        try {
                            System.out.println("Отключение пользователя!");
                            in.close();
                            notifyConnections("Подключен клиент: ip: " + clientSocket.getInetAddress().getHostAddress() + ", порт: " + clientSocket.getPort(), 0);
                            this.clientSocket.close();
                            flag = false;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    case "Модели":
                        result += makeSql.modelGetName("table_fuel");
                        result += "///";
                        result += makeSql.modelGetName("table_battery");
                        result += "///";
                        result += makeSql.modelGetName("table_carcase");
                        result += "///";
                        result += makeSql.modelGetName("table_wheels");
                        result += "///";
                        out.println(result);
                        System.out.println("Данные по моделям!");
                        break;
                    case "Продукты":
                        result = makeSql.getAllProducts();
                        if (result.equals("")) {
                            out.println("Нет данных");
                        } else {
                            out.println(result);
                        }
                        System.out.println("Данные по продуктам!");
                        break;
                    default:
                        String[] args = line.split("; ");
                        switch (args[0]) {
                            case "Изменение" -> {
                                makeSql.editProduct(args[1], args[2], args[3], args[4], args[5], args[6]);
                                result = "updated";
                                System.out.println("Изменения данных продукта");
                            }
                            case "Авторизация" -> {
                                result = makeSql.Authorization(args[1], args[2]);
                                System.out.println("Авторизация пользователя!");
                            }
                            case "Регистрация" -> {
                                makeSql.insertNewClient(args[1], args[2], args[3]);
                                result = "added";
                                System.out.println("Регистрация нового пользователя");
                            }
                            case "Добавление" -> {
                                makeSql.insertNewProduct(args[1], args[2], args[3], args[4], args[5]);
                                result = "added";
                                System.out.println("Добавление нового продукта");
                            }
                            case "Удаление" -> {
                                makeSql.deleteProduct(Integer.parseInt(args[1]));
                                System.out.println("Удаление выбранного продукта");
                                result = makeSql.getAllProducts();
                                if (result.equals("")) {
                                    out.println("Нет данных");
                                } else {
                                    out.println(result);
                                }
                                System.out.println("Данные по продуктам!");
                            }
                            case "Отчет" -> {
                                String baseValues = "";
                                String productValues = "";
                                productValues = getProductValues(makeSql, args[1]);
                                // о продукте
                                result += productValues;
                                baseValues = getBaseValues(makeSql);
                                // о базовых значениях
                                result += baseValues;
                                String[] baseFields = baseValues.split("///");
                                String[] productFields = productValues.split("///");
                                // резултаты оценки качества
                                String quality = countQuality(baseFields, productFields);
                                result += quality;
                                String[] productQuality = quality.split("///");
                                // резултаты дифференциального метода оценки
                                for (int i = 0; i < 4; i++) {
                                    result += DiffMethod(productQuality[i]);
                                    result += "///";
                                }
                                System.out.println(result);
                            }
                        }
                        out.println(result);

                }
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public void addConnection(Connection connection) {
        this.connections.add(connection);
    }

    @Override
    public void notifyConnections(String message, int flag) {
        for (Connection connection : connections) {
            connection.handleEvent(message, flag);
        }
    }
}

