package Client.controllers;

import Client.model.Client;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class clientFormController implements Initializable {
    public AnchorPane client_ap_main;
    public Button client_button_send;
    public TextField client_tf_message;
    public ScrollPane client_sp_main;
    public VBox client_vBox_message;
    public Button client_button_addImage;

    private Client client;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            client = new Client(new Socket("localhost",5000));
            System.out.println("Connect to server");
        } catch (IOException e) {
            e.printStackTrace();
        }

        client_vBox_message.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                client_sp_main.setVvalue((Double) newValue);
            }
        });

        client.receiveMassegeFromSever(client_vBox_message);

        client_button_send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String messageToSend = client_tf_message.getText();
                if(!messageToSend.isEmpty()) {
                    HBox h_Box = new HBox();
                    h_Box.setAlignment(Pos.CENTER_RIGHT);
                    h_Box.setPadding(new Insets(2, 2, 2, 2));

                    Text text = new Text(messageToSend);
                    text.setFont(new Font("SansSerif", 20));

                    TextFlow textFlow = new TextFlow(text);
                    textFlow.setStyle("-fx-background-color : rgb(18,60,198); -fx-background-radius : 20px;");

                    textFlow.setPadding(new Insets(2, 2, 2, 2));
                    text.setFill(Color.color(0.934,0.945,0.996));

                    h_Box.getChildren().add(textFlow);
                    client_vBox_message.getChildren().add(h_Box);

                    client.sendMessageOrImageToServer(messageToSend);
                    client_tf_message.clear();
                }
            }
        });

        FileChooser fileChooser = new FileChooser();
        client_button_addImage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_RIGHT);
                hBox.setPadding(new Insets(5,5,5,10));

                File file = fileChooser.showOpenDialog(null);

                ImageView imageView = new ImageView(file.toURI().toString());
                imageView.setFitHeight(80.0);
                imageView.setFitWidth(80.0);

                hBox.getChildren().add(imageView);
                client_vBox_message.getChildren().add(hBox);

                client.sendMessageOrImageToServer(file.toURI().toString());
            }
        });
    }

    public static void addLabel(String messageFromServer,VBox vBox){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5,5,5,10));

        if(!(checkValidation(messageFromServer))){
            Text text = new Text(messageFromServer);
            text.setFont(new Font("SansSerif", 20));

            TextFlow textFlow = new TextFlow(text);

            textFlow.setStyle("-fx-background-color: rgb(233,233,235);"+
                    "-fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5,10,5,10));

            hBox.getChildren().add(textFlow);
        }else {
            ImageView imageView = new ImageView(messageFromServer);
            imageView.setFitHeight(80.0);
            imageView.setFitWidth(80.0);

            hBox.getChildren().add(imageView);
        }


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBox.getChildren().add(hBox);
            }
        });
    }

    private static boolean checkValidation(String messageFromServer) {
        return messageFromServer.matches("(.*):(.*)");
    }
}
