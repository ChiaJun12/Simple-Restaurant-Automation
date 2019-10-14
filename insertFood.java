package restaurant_automation_v.pkg2;

import java.awt.Desktop;
import javafx.scene.image.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class insertFood extends Application {
    ImageView imageview;
    Desktop desktop = Desktop.getDesktop();
    Stage window;
    Pane pane;
    File file;
    GridPane insertPage = new GridPane();
    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        FileChooser fc = new FileChooser();
            
        insertPage.setHgap(10);
        insertPage.setVgap(10);
        insertPage.setAlignment(Pos.CENTER);
        
        Label food_id = new Label("Food ID: ");
        Label food_name = new Label("Food Name: ");
        Label food_price = new Label("Food Price: ");
        Label food_image = new Label("Food Image: ");
        
        TextField id_input = new TextField();
        TextField name_input = new TextField();
        TextField price_input = new TextField();
        
        Button imageInsert = new Button("Import Image");
        Button insert = new Button("Done");
        insert.setId("btn");
        
        GridPane.setConstraints(food_id, 0, 0);
        GridPane.setConstraints(food_name, 0, 1);
        GridPane.setConstraints(food_price, 0, 2);
        GridPane.setConstraints(food_image, 0, 3);
        GridPane.setConstraints(id_input, 1, 0);
        GridPane.setConstraints(name_input, 1, 1);
        GridPane.setConstraints(price_input, 1, 2);
        GridPane.setConstraints(imageInsert, 1, 3);
        GridPane.setConstraints(insert, 0, 4);
        
        insertPage.getChildren().addAll(food_id, food_name, food_price, food_image, id_input, name_input, price_input, imageInsert, insert);
        Scene scene = new Scene(insertPage, 300, 300);
        scene.getStylesheets().add(getClass().getResource("insertFood.css").toExternalForm());
        
        imageInsert.setOnAction(e -> {
            configureFileChooser(fc);
            file = fc.showOpenDialog(window);
            
            if(file != null){
                try {
                    openFile(file);
                } catch (IOException ex) {
                    Logger.getLogger(insertFood.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        insert.setOnAction(e -> {
            try {
                upload(id_input.getText(), name_input.getText(), price_input.getText(), file);
                id_input.setText("");
                name_input.setText("");
                price_input.setText("");
            } catch (SQLException | FileNotFoundException ex) {
                Logger.getLogger(insertFood.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        window.setScene(scene);
        window.setTitle("Insert Food");
        window.show();
    }
    
    public void openFile(File path) throws IOException{
        Image image = new Image(new FileInputStream(path));
        ImageView imv = new ImageView(image);
        imv.setFitHeight(100);
        imv.setFitWidth(100);
        
        Group root = new Group(imv);
        GridPane.setConstraints(root, 1, 3);
        insertPage.getChildren().add(root);
    }
    
    public void configureFileChooser(FileChooser fc){
        fc.setTitle("View Picture");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG", "*.jpg"));
    }
    
    public void upload(String id, String name, String price, File path) throws SQLException, FileNotFoundException {
        FileInputStream fin = new FileInputStream(path);
        Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/restaurant?useTimezone=true&serverTimezone=UTC", "root", null);
        String sql = "INSERT INTO food_description (food_id, food_name, food_price, food_image) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, id);
        pstmt.setString(2, name);
        pstmt.setDouble(3, Double.parseDouble(price));
        pstmt.setBinaryStream(4, fin);
        pstmt.executeUpdate();
        System.out.print("Item Uploaded");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
