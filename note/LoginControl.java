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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginControl  implements Initializable {

	@FXML private TextField tID;
	@FXML private PasswordField pPW;
	@FXML private Button bLogin;
	@FXML private Button bSignin;
	@FXML private Button bPassword;
	
	String url = "jdbc:mysql://localhost:3306/note?useUnicode=true&characterEncoding=utf8";
	String id = "root";
	String pass = "qwer";
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	PreparedStatement pstmt = null;

	private Stage primaryStage;	
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	static String loginID;

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
	void shortCut(String fxml) {
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource(fxml + ".fxml"));
			AnchorPane next = (AnchorPane) bLogin.getScene().getRoot();
			next.getChildren().clear();
			next.getChildren().add(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void dialog(String text) throws IOException {
		Stage dialog = new Stage(StageStyle.UTILITY);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(primaryStage);
		dialog.setTitle("확인");
		
		Parent parent = FXMLLoader.load(getClass().getResource("custom_dialog.fxml"));
		Label txtTitle = (Label) parent.lookup("#txtTitle");
		txtTitle.setText(text);
		Button btnOk = (Button) parent.lookup("#btnOk");
		btnOk.setOnAction(eventDlg->dialog.close());	
		Scene scene = new Scene(parent);
		
		dialog.setScene(scene);
		dialog.setResizable(false);
		dialog.show();
	}
	
	public void initialize(URL location, ResourceBundle resources) {
		bLogin.setOnAction(event->bLoginAction(event));
		bSignin.setOnAction(event->bSigninAction(event));
		bPassword.setOnAction(event->bPasswordAction(event));
	}
	
	private void bPasswordAction(ActionEvent event) {
		shortCut("Password");
	}
	private void bLoginAction(ActionEvent event) {
		connect();
		boolean idCheck = false;
		try {
			String query = "SELECT * FROM member";

			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			while (rs.next()) {
				if (rs.getString("id").equals(tID.getText()) && rs.getString("pw").equals(pPW.getText())) {
					idCheck = true;
				}
			}
			rs.close();
			if (idCheck == true) {
				loginID = tID.getText();
				shortCut("Home");
			}
			else {
				dialog("정보를 다시 확인하세요.");
			}
			try {
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} catch (SQLException | IOException e1) {
			e1.printStackTrace();
		}
		disConnect();
	}
	private void bSigninAction(ActionEvent event) {
		shortCut("Signin");
	}
	
}