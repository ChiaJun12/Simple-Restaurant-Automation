package restaurant_automation_v.pkg2;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javax.imageio.ImageIO;

public class Menu extends Application {
    Stage window;
    ArrayList<String> food_name = new ArrayList<>();
    ArrayList<Double> food_price = new ArrayList<>();
    ArrayList<Image> food_image = new ArrayList<>();
    
    ArrayList<String> selected_food = new ArrayList<>();
    ArrayList<String> selected_food_price = new ArrayList<>();
    
    double total_price = 0.00;
    TextField total = new TextField();
    
    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle("Menu");
        
        BorderPane border = new BorderPane();
        border.setId("bg");
        VBox vx = new VBox();
        vx.setId("vbox");
        Label topic = new Label("Japanese");
        topic.setId("topic");
        topic.setAlignment(Pos.TOP_CENTER);
        Button noodle = new Button("Noodle");
        Button rice = new Button("Rice");
        Button sashimi = new Button("Sashimi");
        Button beverage = new Button("Beverage");
        Button drink = new Button("Drink");
        Button sushi = new Button("Sushi");
        Button checkout = new Button("Checkout");
        
        noodle.setId("nav");
        rice.setId("nav");
        sashimi.setId("nav");
        sushi.setId("nav");
        drink.setId("nav");
        beverage.setId("nav");
        checkout.setId("checkout");
        total.setId("total");
        
        noodle.setOnAction(e -> {
            setCategories(border, window, "Noodle");
        });
        
        rice.setOnAction(e -> {
            setCategories(border, window, "Rice");
        });
        
        sashimi.setOnAction(e -> {
            setCategories(border, window, "Sashimi");
        });
        
        sushi.setOnAction(e -> {
            setCategories(border, window, "Sushi");
        });
        
        drink.setOnAction(e -> {
            setCategories(border, window, "Drink");
        });
        
        beverage.setOnAction(e -> {
            setCategories(border, window, "Beverage");
        });
        
        checkout.setOnAction(e -> {
            checkout payment = new checkout();
            payment.setvalue(selected_food, selected_food_price);
            payment.start(primaryStage);
        });
        
        try{
            putItems(border, window, "Noodle");
        }
        catch(SQLException | IOException ex){
            System.out.print(ex);
        }
        
        vx.getChildren().addAll(topic, noodle, rice, sashimi, beverage, drink, sushi, total, checkout);
        border.setLeft(vx);
        Scene scene = new Scene(border, 1024, 768);
        scene.getStylesheets().add(getClass().getResource("Menu.css").toExternalForm());
        window.setScene(scene);
        window.show();
    }
    
    public void setCategories(BorderPane border, Stage window, String ctg){
        try{
            putItems(border, window, ctg);
        }
        catch(SQLException | IOException ex){
            System.out.print(ex);
        }
    }

    public void putItems(BorderPane border, Stage window, String Catg) throws SQLException, IOException{
        food_name.clear();
        food_price.clear();
        food_image.clear();
        
        FlowPane fp = new FlowPane();
        fp.setVgap(10);
        fp.setHgap(20);
        
        Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/restaurant?useTimezone=true&serverTimezone=UTC", "root", null);
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT food_name, food_price, food_image FROM `food_description` WHERE food_name LIKE \"" + Catg + "%\"");
        
        while(rs.next()){
            food_name.add(rs.getString(1));
            food_price.add(rs.getDouble(2));
            java.sql.Blob blob = rs.getBlob(3);
            InputStream input = blob.getBinaryStream();
            BufferedImage image = ImageIO.read(input);
            Image img = SwingFXUtils.toFXImage(image, null);
            food_image.add(img);
        } 
        
        for(int i=0; i<food_name.size(); i++){
            Button btn = new Button();
            double price = food_price.get(i);
            String name = food_name.get(i);
            ImageView iv = new ImageView();
            iv.setImage(food_image.get(i));
            iv.setFitWidth(150);
            iv.setFitHeight(200);
            btn.setId("btn");
            btn.setGraphic(iv);
            Label label = new Label(food_name.get(i) + "\n\nRM " + food_price.get(i) + "0");
            label.setId("label");
            btn.setOnAction(e -> {
                total_price += price;
                total.setText(String.format("RM %.2f", total_price));
                selected_food.add(name);
                selected_food_price.add(Double.toString(price));
                //setOrder order = new setOrder();
                //order.setFoodName(name);
                //order.start(window);
            });
            fp.getChildren().addAll(btn, label);
        }
        border.setCenter(fp);
        BorderPane.setMargin(fp, new Insets(50, 0, 0, 50));
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
