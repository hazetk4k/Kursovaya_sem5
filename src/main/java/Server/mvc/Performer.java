package Server.mvc;


import Server.mvc.Interfaces.Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Performer implements Connection {
    public boolean serverStatus = true;

    public List<String> clientsStatus = new ArrayList<>();

    public List<String> getClientsStatus() {
        return clientsStatus;
    }

    public boolean isServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(boolean serverStatus) {
        this.serverStatus = serverStatus;
    }

    public Performer()
    {}

    @Override
    public void handleEvent(String connections, int flag) {
        if (flag == 1){
            this.clientsStatus.add(connections);
        } else {
            this.clientsStatus.remove(connections);
        }
    }


    public class ServerStatusThread implements Runnable {
        Thread thread;
        ServerSocket server;

        public ServerStatusThread(ServerSocket server) {
            this.server = server;
            thread = new Thread(this, "ServerStatusThread");
            thread.start();

        }

        @Override
        public void run() {
            while (isServerStatus()) {
                try {
                    thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                this.server.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public void ModelMain() {
        ServerSocket server = null;
        try {
            try {
                server = new ServerSocket(3311);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                server.setReuseAddress(true);
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
            Socket client = null;
            ServerStatusThread serverStatusThread = new ServerStatusThread(server);
            while (isServerStatus()) {
                try {
                    client = server.accept();
                } catch (IOException e) {
                    break;
                }
                ClientHandler clientSock = new ClientHandler(client);
                clientSock.addConnection(this);
            }
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}

