package controller;

import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tm.ToDoTM;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class ToDoFormController {

    public Label lblTitle;
    public Label lblUserId;
    public AnchorPane root;
    public Pane subRoot;
    public TextField txtDescription;
    public ListView<ToDoTM> lstToDo;
    public TextField txtSelectedToDo;
    public Button btnDelete;
    public Button btnUpdate;

    public void initialize(){
        lblTitle.setText("Hi "+LoginFormController.loginUserName+" Welcome to To Do List");
        lblUserId.setText(LoginFormController.loginUserID);

        subRoot.setVisible(false);

        loadList();


        setDisableCommon(true);
        lstToDo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTM>() {
            @Override
            public void changed(ObservableValue<? extends ToDoTM> observable, ToDoTM oldValue, ToDoTM newValue) {
                if(lstToDo.getSelectionModel().getSelectedItem()==null){
                    return;
                }

               setDisableCommon(false);
               subRoot.setVisible(false);

               txtSelectedToDo.setText(lstToDo.getSelectionModel().getSelectedItem().getDescription());
            }
        });

    }
    public void setDisableCommon(boolean isDisable){
        txtSelectedToDo.setDisable(isDisable);
        btnDelete.setDisable(isDisable);
        btnUpdate.setDisable(isDisable);

        txtSelectedToDo.clear();
    }
    public void btnLogOutOnAction(ActionEvent event) throws IOException {
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION,"Do you want to log out?", ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){
            Parent parent= FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
            Scene scene=new Scene(parent);
            Stage primaryStage= (Stage) root.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
            primaryStage.centerOnScreen();
        }

    }

    public void btnAddNewToDoOnAction(ActionEvent event) {

        setDisableCommon(true);
        lstToDo.getSelectionModel().clearSelection();
        subRoot.setVisible(true);
        txtDescription.requestFocus();
    }

    public void btnAddToListOnAction(ActionEvent event) {
        String id=autoGenerateID();
        String description = txtDescription.getText();
        String userId = lblUserId.getText();

        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into todo values(?,?,?)");
            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,description);
            preparedStatement.setObject(3,userId);

            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        txtDescription.clear();
        subRoot.setVisible(false);
        loadList();


    }

    public String autoGenerateID() {
        Connection connection = DBConnection.getInstance().getConnection();

        String id="";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from todo order by id desc limit 1 ");

            boolean isExist = resultSet.next();

            if (isExist) {
                String todoID = resultSet.getString(1);
                todoID = todoID.substring(1);

                int intID = Integer.parseInt(todoID);

                intID++;

                if (intID < 10) {
                   id= "T00" + intID;
                } else if (intID < 100) {
                   id= "T0" + intID;
                } else {
                   id="T" + intID;
                }
            } else {
                   id= "T001";
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return id;

    }
    public void loadList(){
        ObservableList<ToDoTM> todoS = lstToDo.getItems();
        todoS.clear();

        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement prepareStatement = connection.prepareStatement("select * from todo where user_id=?");

            prepareStatement.setObject(1,lblUserId.getText());

            ResultSet resultSet = prepareStatement.executeQuery();

            while (resultSet.next()){
                String id=resultSet.getString(1);
                String description = resultSet.getString(2);
                String user_id = resultSet.getString(3);

                todoS.add(new ToDoTM(id,description,user_id));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }
}
