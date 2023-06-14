package note;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ViewControl implements Initializable {

	@FXML private Border frame;
	@FXML private ImageView imageView;
	@FXML private Button bHome;
	@FXML private Button bRecord;
	@FXML private Button bEvent;
	@FXML private Button bEdit;
	@FXML private Button bDelete;
	@FXML private TextField tTitle;
	@FXML private TextArea tNote;
	@FXML private Label lDate;
	@FXML private MenuButton menu;
	@FXML private MenuItem bLogOut;
	@FXML private MenuItem bLogEdit;
	@FXML private MenuItem bSignOut;
	@FXML private DatePicker datePicker;
	
	String url = "jdbc:mysql://localhost:3306/note?useUnicode=true&characterEncoding=utf8";
	String id = "root";
	String pass = "qwer";
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	PreparedStatement pstmt = null;
	
	SimpleDateFormat dateform = new SimpleDateFormat("yyyy-MM-dd");

	String path = System.getProperty("user.dir");
	String imagePath = "file:/" + path + "/src/note/image/" + LoginControl.loginID + "/";
	String imagefile;
	
	private Stage primaryStage;	
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	
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
			String query = "SELECT * FROM record where image = '" + MemoryControl.select + "'";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			

			while (rs.next()) {
				tTitle.setText(rs.getString("title"));
				tNote.setText(rs.getString("note"));
				lDate.setText(dateform.format(rs.getDate("date")));
				imageView.setImage(new Image(imagePath + rs.getString("image")));
				imagefile = path + "/src/note/image/" + LoginControl.loginID + "/" + rs.getString("image");
			}

			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	void delete() {
		try	{
			String query = "delete from record where image = '" + MemoryControl.select + "'";
			pstmt = conn.prepareStatement(query);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Path filePath = Paths.get(imagefile);
		try {
			Files.delete(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	void dialog(String text) {
		Stage dialog = new Stage(StageStyle.UTILITY);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(primaryStage);
		dialog.setTitle("확인");
		
		Parent parent = null;
		try {
			parent = FXMLLoader.load(getClass().getResource("custom_dialog.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Label txtTitle = (Label) parent.lookup("#txtTitle");
		txtTitle.setText(text);
		Button btnOk = (Button) parent.lookup("#btnOk");
		btnOk.setOnAction(eventDlg->dialog.close());	
		Scene scene = new Scene(parent);
		
		dialog.setScene(scene);
		dialog.setResizable(false);
		dialog.show();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bEdit.setOnAction(event->bEditAction(event));
		bDelete.setOnAction(event->bDeleteAction(event));
		bHome.setOnAction(event->bHomeAction(event));
		bRecord.setOnAction(event->bRecordAction(event));
		bEvent.setOnAction(event->bEventAction(event));
		
		bLogOut.setOnAction(event->bLogOutAction(event));
		bLogEdit.setOnAction(event->bLogEditAction(event));
		bSignOut.setOnAction(event->bSignOutAction(event));
		
		connect();
		data();
		disConnect();
		
		tTitle.setEditable(false);
		tNote.setEditable(false);
		datePicker.setDisable(true);
		
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
	private void bEditAction(ActionEvent event) {
		shortCut("Edit");
	}
	private void bDeleteAction(ActionEvent event) {
		connect();
		delete();
		disConnect();
		shortCut("Home");
		dialog("삭제했습니다.");
	}
	private void bRecordAction(ActionEvent event) {
		shortCut("Record");
	}
	private void bEventAction(ActionEvent event) {
		shortCut("Memory");
	}
	private void bHomeAction(ActionEvent event) {
		shortCut("Home");
	}
}
