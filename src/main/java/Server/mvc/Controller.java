package Server.mvc;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.List;

public class Controller {
    public Button btnRefresh;
    Performer performer = new Performer();
    @FXML
    private Button stopServer;
    @FXML
    private Button startServer;
    @FXML
    private ListView<String> ConnectionInfo = new ListView<String>();
    @FXML
    private TextField btnPort;
    @FXML
    private TextField btnIP;

    public void onBtnRefresh(ActionEvent actionEvent) {
        List<String> listOfConnections = performer.getClientsStatus();
        this.ConnectionInfo.getItems().clear();
        for(int i = 0; i <= listOfConnections.size() - 1; i++){
            this.ConnectionInfo.getItems().add(listOfConnections.get(i));
        }
    }

    class StartThread implements Runnable {

        Thread tread;

        StartThread() {
            tread = new Thread(this, "ServerThread");
            tread.start();
        }

        @Override
        public void run() {
            performer.ModelMain();
        }
    }

    public void OnButtonStop(ActionEvent actionEvent) {
        this.ConnectionInfo.getItems().clear();
        this.ConnectionInfo.getItems().add("Сервер выключен!");
        this.startServer.setDisable(false);
        this.stopServer.setDisable(true);
        this.btnPort.setDisable(false);
        performer.serverStatus = false;
    }


    public void OnButtonStart(ActionEvent actionEvent) {
        performer.serverStatus = true;
        this.ConnectionInfo.getItems().clear();
        this.ConnectionInfo.getItems().add("Сервер запущен!");
        this.startServer.setDisable(true);
        this.stopServer.setDisable(false);
        this.btnPort.setDisable(true);
        StartThread startThread = new StartThread();
    }
}