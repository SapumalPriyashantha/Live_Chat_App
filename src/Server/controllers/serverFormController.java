package Server.controllers;

import Server.model.Server;
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
import java.net.ServerSocket;
import java.net.URL;
import java.util.ResourceBundle;

public class serverFormController implements Initializable {
    public AnchorPane server_ap_main;
    public Button server_button_send;
    public TextField sever_tf_message;
    public ScrollPane sever_sp_main;
    public VBox server_vBox_message;
    public Button server_button_addImage;

    private Server server;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
            server = new Server(new ServerSocket(5000));
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Error creating server");
        }

        server_vBox_message.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                sever_sp_main.setVvalue((Double) newValue);
            }
        });

        server.receiveMessageFromClient(server_vBox_message);

        server_button_send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String messageToSend = sever_tf_message.getText();
                if(!messageToSend.isEmpty()){
                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER_RIGHT);
                    hBox.setPadding(new Insets(5,5,5,5));

                    Text text = new Text(messageToSend);
                    text.setFont(new Font("SansSerif", 20));

                    TextFlow textFlow = new TextFlow(text);
                    textFlow.setStyle("-fx-background-color : rgb(18,60,198); -fx-background-radius : 20px;");
                    textFlow.setPadding(new Insets(5,5,5,5));
                    text.setFill(Color.color(0.934,0.945,0.996));

                    hBox.getChildren().add(textFlow);
                    server_vBox_message.getChildren().add(hBox);

                    server.sendMessageOrImageToClient(messageToSend);
                    sever_tf_message.clear();
                }
            }
        });

        FileChooser fileChooser = new FileChooser();
        server_button_addImage.setOnAction(new EventHandler<ActionEvent>() {
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
                server_vBox_message.getChildren().add(hBox);

                server.sendMessageOrImageToClient(file.toURI().toString());
            }
        });
    }

    public static void addLable(String messageFromClient,VBox vBox){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5,5,5,10));

        if(!(checkValidation(messageFromClient))){
            Text text = new Text(messageFromClient);
            TextFlow textFlow = new TextFlow(text);
            text.setFont(new Font("SansSerif", 20));

            textFlow.setStyle("-fx-background-color: rgb(233,233,235);"+
                    "-fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5,10,5,10));

            hBox.getChildren().add(textFlow);
        }else {
            ImageView imageView = new ImageView(messageFromClient);
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

    private static boolean checkValidation(String messageFromClient) {
        return messageFromClient.matches("(.*):(.*)");
    }
}
