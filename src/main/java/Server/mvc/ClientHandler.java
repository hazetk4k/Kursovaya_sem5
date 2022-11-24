package Server.mvc;

import Server.mvc.Interfaces.Connected;
import Server.mvc.Interfaces.Connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
                String line = in.readLine();
                switch (line) {
                    case "Disconnect":
                        try {
                            in.close();
                            notifyConnections("Подключен клиент: ip: " + clientSocket.getInetAddress().getHostAddress() + ", порт: " + clientSocket.getPort(), 0);
                            this.clientSocket.close();
                            flag = false;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            } catch (IOException e) {
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

