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

public class SigninControl implements Initializable {
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
	@FXML private PasswordField pPW;
	@FXML private TextField tName;
	@FXML private TextField tcontact;
	@FXML private Button bSignIn;
	@FXML private Button bCancel;
	
	private Stage primaryStage;	
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	
	public void initialize(URL location, ResourceBundle resources) {
		bSignIn.setOnAction(event->{
			try {
				bSigninAction(event);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		bCancel.setOnAction(event->bCancelAction(event));
	}
	
	private void bSigninAction(ActionEvent event) throws IOException {
		connect();
		boolean idCheck = true;

		if (tID.getText().equals("") == true || pPW.getText().equals("") == true || tName.getText().equals("") == true || tcontact.getText().equals("") == true) {
			dialog("정보를 모두 입력하시오");
			idCheck = false;
		}
		try {
			String query = "SELECT * FROM member";

			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			while (rs.next()) {
				if (tID.getText().equals(rs.getString("ID"))) {
					dialog("아이디가 중복되었습니다.");
					idCheck = false;
				}
			}
			
			rs.close();
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		if (idCheck == true) {
			try {
				String Query = "Insert into member values (default, ?, ?, ?, ?)";
				pstmt = conn.prepareStatement(Query);

				pstmt.setString(1, tID.getText());
				pstmt.setString(2, pPW.getText());
				pstmt.setString(3, tName.getText());
				pstmt.setString(4, tcontact.getText());
				pstmt.executeUpdate();
				pstmt.close();

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			try {
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			shortCut("Login");
			dialog("회원가입되었습니다.");
		}
		disConnect();
	}
	private void bCancelAction(ActionEvent event) {
		shortCut("Login");
	}
	
}
