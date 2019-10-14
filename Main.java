package restaurant_automation_v.pkg2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {
    Stage window;
    @Override
    public void start(Stage primaryStage) {
        main_page(primaryStage);
    }
    
    private void main_page(Stage primaryStage){
        window = primaryStage;
        window.setTitle("Setting Table.....");
        
        GridPane table_page = new GridPane();
        Label table_id = new Label("Enter Table ID: ");
        TextField table_input = new TextField("");
        
        Button confirm = new Button("Confirm");
        confirm.setOnAction(e -> {
            String input = table_input.getText();
            if(input.equals("InsertItems")){
                insertFood isFood = new insertFood();
                isFood.start(window);
            }
            else{
                try{
                    checkTable(input);
                }
                catch(SQLException ex){
                    System.out.println(ex);
                }
            }
        });
        
        GridPane.setConstraints(table_id, 0, 0);
        GridPane.setConstraints(table_input, 1, 0);
        GridPane.setConstraints(confirm, 1, 1);
        table_page.setVgap(10);
        table_page.setHgap(10);
        table_page.setAlignment(Pos.CENTER);
        
        table_page.getChildren().addAll(table_id, table_input, confirm);
        Scene scene = new Scene(table_page, 300, 200);
        window.setScene(scene);
        window.show();
    }

    public void checkTable(String table_num) throws SQLException{
        Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/restaurant?useTimezone=true&serverTimezone=UTC", "root", "");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT status FROM `tables` WHERE table_id = \"" + table_num + "\"");
        
        if(!rs.next()){
            setTable(table_num);
            Menu menu = new Menu();
            menu.start(window);
        }
        
        if(!rs.getBoolean(1)){
            Alert error = new Alert(AlertType.ERROR);
            error.setHeaderText("Table Not Available");
            error.setContentText("There're people still using the table");
            error.showAndWait();
        }
        else{
            String sql = "UPDATE `tables` SET status = (?) WHERE table_id = \"" + table_num + "\"";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setBoolean(1, false);
            pstmt.executeUpdate();
            
            Menu menu = new Menu();
            menu.start(window);
        }
    }
    
    public void setTable(String table_num) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/restaurant?useTimezone=true&serverTimezone=UTC", "root", "");
        
        String sql = "INSERT INTO `tables` (table_id) VALUES (\"" + table_num + "\")";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.executeUpdate();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
