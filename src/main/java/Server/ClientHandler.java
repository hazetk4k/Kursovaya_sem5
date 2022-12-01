package Server;

import Server.Interfaces.Connected;
import Server.Interfaces.Connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

