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

public class PasswordControl implements Initializable {
	String url = "jdbc:mysql://localhost:3306/note?useUnicode=true&characterEncoding=utf8";
	String id = "root";
	String pass = "qwer";
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	PreparedStatement pstmt = null;

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
			AnchorPane next = (AnchorPane) bCancel.getScene().getRoot();
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
	
	@FXML private TextField tID;
	@FXML private TextField tPW;
	@FXML private TextField tName;
	@FXML private TextField tcontact;
	@FXML private Button bPassword;
	@FXML private Button bCancel;
	
	private Stage primaryStage;	
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	
	public void initialize(URL location, ResourceBundle resources) {
		tPW.setVisible(false);
		bPassword.setOnAction(event->bSigninAction(event));
		bCancel.setOnAction(event->bCancelAction(event));
	}
	
	private void bSigninAction(ActionEvent event) {
		connect();
		boolean idCheck = true;
		boolean pwCheck = false;

		if (tID.getText().equals("") == true || tName.getText().equals("") == true || tcontact.getText().equals("") == true) {
			dialog("정보를 모두 입력하시오");
			idCheck = false;
		}
		if (idCheck == true) {
			try {
				String query = "SELECT * FROM member";
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query);

				while (rs.next()) {
					if (tID.getText().equals(rs.getString("id")) && tName.getText().equals(rs.getString("name")) && tcontact.getText().equals(rs.getString("contact"))) {
						tPW.setText(rs.getString("pw"));
						tPW.setVisible(true);
						pwCheck = true;
					}
				}
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (pwCheck == true) {
				dialog("찾았습니다.");
			}
			else if (pwCheck == false) {
				dialog("정보가 일치하지 않습니다.");
			}
		}
		disConnect();
	}
	private void bCancelAction(ActionEvent event) {
		shortCut("Login");
	}
	
}
