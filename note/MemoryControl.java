package note;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MemoryControl  implements Initializable {
	
	@FXML private ImageView image1;
	@FXML private ImageView image2;
	@FXML private ImageView image3;
	@FXML private ImageView image4;
	@FXML private ImageView image5;
	@FXML private ImageView image6;
	@FXML private ImageView image7;
	@FXML private ImageView image8;
	@FXML private ImageView image9;
	@FXML private ImageView image10;
	@FXML private ImageView image11;
	@FXML private ImageView image12;
	@FXML private Button bHome;
	@FXML private Button bEvent;
	@FXML private Button bCancel;
	@FXML private Button bRecord;
	@FXML private Button bLeft;
	@FXML private Button bRight;
	@FXML private MenuButton menu;
	@FXML private MenuItem bLogOut;
	@FXML private MenuItem bLogEdit;
	@FXML private MenuItem bSignOut;
	@FXML private Label lPage;
	
	String url = "jdbc:mysql://localhost:3306/note?useUnicode=true&characterEncoding=utf8";
	String id = "root";
	String pass = "qwer";
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	PreparedStatement pstmt = null;
	
	String path = System.getProperty("user.dir");
	String imagePath = "file:/" + path + "/src/note/image/" + LoginControl.loginID + "/";
	String image[] = new String[1200];
	static String select;
	int i = 0;
	static int page = 0;

	void connect() {
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			conn = DriverManager.getConnection(url, id, pass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	void disConnect() {
		try {
			conn.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	void data() {
		try {
			String query = "SELECT * FROM record where id = '" + LoginControl.loginID + "' order by date DESC";

			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			

			while (rs.next()) {
				image[i] = rs.getString("image");
				i++;
			}

			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	void setImage(int i) {
		image1.setImage(new Image(imagePath + image[i]));
		centerImage(image1);
		image2.setImage(new Image(imagePath + image[i+1]));
		centerImage(image2);
		image3.setImage(new Image(imagePath + image[i+2]));
		centerImage(image3);
		image4.setImage(new Image(imagePath + image[i+3]));
		centerImage(image4);
		image5.setImage(new Image(imagePath + image[i+4]));
		centerImage(image5);
		image6.setImage(new Image(imagePath + image[i+5]));
		centerImage(image6);
		image7.setImage(new Image(imagePath + image[i+6]));
		centerImage(image7);
		image8.setImage(new Image(imagePath + image[i+7]));
		centerImage(image8);
		image9.setImage(new Image(imagePath + image[i+8]));
		centerImage(image9);
		image10.setImage(new Image(imagePath + image[i+9]));
		centerImage(image10);
		image11.setImage(new Image(imagePath + image[i+10]));
		centerImage(image11);
		image12.setImage(new Image(imagePath + image[i+11]));
		centerImage(image12);
	}
	void shortCut(String fxml) {
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource(fxml + ".fxml"));
			AnchorPane next = (AnchorPane) bHome.getScene().getRoot();
			next.getChildren().clear();
			next.getChildren().add(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	void showRecord(int i) {
		select = image[i];
		shortCut("View");
	}
	
	public void centerImage(ImageView imageView) {
        Image img = imageView.getImage();
        if (img != null) {
            double w = 0;
            double h = 0;

            double ratioX = imageView.getFitWidth() / img.getWidth();
            double ratioY = imageView.getFitHeight() / img.getHeight();

            double reducCoeff = 0;
            if(ratioX >= ratioY) {
                reducCoeff = ratioY;
            } else {
                reducCoeff = ratioX;
            }

            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;

            imageView.setX((imageView.getFitWidth() - w) / 2);
            imageView.setY((imageView.getFitHeight() - h) / 2);

        }
    }
	
	public void initialize(URL location, ResourceBundle resources) {
		connect();
		data();
		disConnect();
		
		bRecord.setOnAction(event->bRecordAction(event));
		bEvent.setOnAction(event->bEventAction(event));
		bHome.setOnAction(event->bHomeAction(event));
		
		bLogOut.setOnAction(event->bLogOutAction(event));
		bLogEdit.setOnAction(event->bLogEditAction(event));
		bSignOut.setOnAction(event->bSignOutAction(event));
		
		bLeft.setOnAction(event->bLeftAction(event));
		bRight.setOnAction(event->bRightAction(event));
		
		setImage(page*12);
		
		if(image[page*12 + 12] == null) {
			bRight.setDisable(true);
		}
		if(page == 0) {
			bLeft.setDisable(true);
		}
		
		lPage.setText(Integer.toString(page + 1) + "page");
		
		if (image1.getImage() != null) {
			image1.setOnMouseClicked(event -> image1Action(event));
		}
		if (image2.getImage() != null) {
			image2.setOnMouseClicked(event -> image2Action(event));
		}
		if (image3.getImage() != null) {
			image3.setOnMouseClicked(event -> image3Action(event));
		}
		if (image4.getImage() != null) {
			image4.setOnMouseClicked(event -> image4Action(event));
		}
		if (image5.getImage() != null) {
			image5.setOnMouseClicked(event -> image5Action(event));
		}
		if (image6.getImage() != null) {
			image6.setOnMouseClicked(event -> image6Action(event));
		}
		if (image7.getImage() != null) {
			image7.setOnMouseClicked(event -> image7Action(event));
		}
		if (image8.getImage() != null) {
			image8.setOnMouseClicked(event -> image8Action(event));
		}
		if (image9.getImage() != null) {
			image9.setOnMouseClicked(event -> image9Action(event));
		}
		if (image10.getImage() != null) {
			image10.setOnMouseClicked(event -> image10Action(event));
		}
		if (image11.getImage() != null) {
			image11.setOnMouseClicked(event -> image11Action(event));
		}
		if (image12.getImage() != null) {
			image12.setOnMouseClicked(event -> image12Action(event));
		}
	}
	
	private void bLeftAction(ActionEvent event) {
		if (page > 1) {
			page --;
			setImage(page*12);
			lPage.setText(Integer.toString(page + 1) + "page");
			bRight.setDisable(false);
		}
		if (page == 1) {
			page --;
			setImage(page*12);
			lPage.setText(Integer.toString(page + 1) + "page");
			bRight.setDisable(false);
			bLeft.setDisable(true);
		}
	}
	private void bRightAction(ActionEvent event) {
		if (page < 98) {
			page ++;
			setImage(page*12);
			lPage.setText(Integer.toString(page + 1) + "page");
			bLeft.setDisable(false);
			if(image[page*12+1] == null) {
				bRight.setDisable(true);
			}
		}
		if (page == 98) {
			page ++;
			lPage.setText(Integer.toString(page + 1) + "page");
			setImage(page*12);
			bLeft.setDisable(false);
			bRight.setDisable(true);
		}
	}
	
	private void image1Action(MouseEvent event) {
		showRecord(page*12 + 0);
	}
	private void image2Action(MouseEvent event) {
		showRecord(page*12 + 1);
	}
	private void image3Action(MouseEvent event) {
		showRecord(page*12 + 2);
	}
	private void image4Action(MouseEvent event) {
		showRecord(page*12 + 3);
	}
	private void image5Action(MouseEvent event) {
		showRecord(page*12 + 4);
	}
	private void image6Action(MouseEvent event) {
		showRecord(page*12 + 5);
	}
	private void image7Action(MouseEvent event) {
		showRecord(page*12 + 6);
	}
	private void image8Action(MouseEvent event) {
		showRecord(page*12 + 7);
	}
	private void image9Action(MouseEvent event) {
		showRecord(page*12 + 8);
	}
	private void image10Action(MouseEvent event) {
		showRecord(page*12 + 9);
	}
	private void image11Action(MouseEvent event) {
		showRecord(page*12 + 10);
	}
	private void image12Action(MouseEvent event) {
		showRecord(page*12 + 11);
	}
	
	private void bLogOutAction(ActionEvent event) {
		LoginControl.loginID = "";
		shortCut("Login");
	}
	private void bSignOutAction(ActionEvent event) {
		shortCut("Signout");
	}
	private void bLogEditAction(ActionEvent event) {
		shortCut("LogEdit");
	}
	
	private void bRecordAction(ActionEvent event) {
		shortCut("Record");
	}
	private void bEventAction(ActionEvent event) {
		shortCut("Event");
	}
	private void bHomeAction(ActionEvent event) {
		shortCut("Home");
	}
}
