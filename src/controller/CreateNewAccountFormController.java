package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class CreateNewAccountFormController {
    public PasswordField txtNewPassword;
    public PasswordField txtConfirmPassword;
    public Label lblPasswordNotMatched1;
    public Label lblPasswordNotMatched2;
    public TextField txtUserName;
    public TextField txtEmail;
    public Button btnRegister;
    public Label lblId;
    public AnchorPane root;



    public void initialize() {
        setVisibility(false);
        setDisableCommon(true);
    }


    public void btnRegisterOnAction(ActionEvent event) {
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (newPassword.equals(confirmPassword)) {
            setBorderColor("transparent");
            setVisibility(false);
            register();

        } else {
            setBorderColor("red");
            txtNewPassword.requestFocus();
            setVisibility(true);
            clearFields();
        }
    }

    public void setBorderColor(String color) {
        txtNewPassword.setStyle("-fx-border-color:" + color);
        txtConfirmPassword.setStyle("-fx-border-color: " + color);
    }

    public void setVisibility(boolean isVisibility) {
        lblPasswordNotMatched1.setVisible(isVisibility);
        lblPasswordNotMatched2.setVisible(isVisibility);
    }

    public void clearFields() {
        txtNewPassword.clear();
        txtConfirmPassword.clear();
    }

    public void btnAddNewUserOnAction(ActionEvent event) {
        setDisableCommon(false);
        txtUserName.requestFocus();
        autoGenerateID();
    }

    public void setDisableCommon(boolean isDisable) {
        txtUserName.setDisable(isDisable);
        txtEmail.setDisable(isDisable);
        txtNewPassword.setDisable(isDisable);
        txtConfirmPassword.setDisable(isDisable);
        btnRegister.setDisable(isDisable);
    }

    public void autoGenerateID() {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select uid from user order by uid desc limit 1 ");

            boolean isExist = resultSet.next();

            if (isExist) {
                String userID = resultSet.getString(1);
                userID = userID.substring(1);

                int intID = Integer.parseInt(userID);

                intID++;

                if (intID < 10) {
                    lblId.setText("U00" + intID);
                } else if (intID < 100) {
                    lblId.setText("U0" + intID);
                } else {
                    lblId.setText("U" + intID);
                }
            } else {
                lblId.setText("U001");
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
    public void register(){
        String id = lblId.getText();
        String userName = txtUserName.getText();
        String email = txtEmail.getText();
        String password = txtConfirmPassword.getText();

        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into user values(?,?,?,?)");
            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,userName);
            preparedStatement.setObject(3,email);
            preparedStatement.setObject(4,password);

            preparedStatement.executeUpdate();

         Parent parent= FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
            Scene scene=new Scene(parent);
            Stage primaryStage= (Stage) root.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.setTitle("Login");


        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }


    }

}
