package Server.mvc;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller {
    Model model = new Model();
    @FXML
    private Button stopServer;
    @FXML
    private Button startServer;
    @FXML
    private TextArea ConnectionInfo;
    @FXML
    private TextField btnPort;
    @FXML
    private TextField btnIP;

    class StartThread implements Runnable{

        Thread tread;
        StartThread(){
            tread=new Thread(this, "ServerThread");
            tread.start();
        }
        @Override
        public void run() {
            model.ModelMain();
        }
    }

    public void OnButtonStop(ActionEvent actionEvent) {
        this.startServer.setDisable(false);
        this.ConnectionInfo.setText("Сервер выключен!");
        this.stopServer.setDisable(true);
        this.btnPort.setDisable(false);
        model.serverStatus = false;
    }

    public void OnButtonStart(ActionEvent actionEvent){
        this.startServer.setDisable(true);
        this.ConnectionInfo.setText("Сервер запущен!");
        this.stopServer.setDisable(false);
        this.btnPort.setDisable(true);
        StartThread startThread = new StartThread();
    }
}