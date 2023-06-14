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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SignoutControl implements Initializable {
	
	@FXML private Button bRecord;
	@FXML private Button bEvent;
	@FXML private Button bHome;
	@FXML private TextField tID;
	@FXML private PasswordField pPW;
	@FXML private TextField tName;
	@FXML private TextField tcontact;
	@FXML private Button bSignOut;
	@FXML private Button bCancel;
	@FXML private MenuButton menu;
	@FXML private MenuItem bLogOut;
	@FXML private MenuItem bLogEdit;
	@FXML private MenuItem bSignOutm;
	
	private Stage primaryStage;	
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	
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
			AnchorPane next = (AnchorPane) bHome.getScene().getRoot();
			next.getChildren().clear();
			next.getChildren().add(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	void data() {
		try {
			String query = "SELECT * FROM member where id = '" + LoginControl.loginID + "'";

			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			while (rs.next()) {
				tID.setText(rs.getString("id"));
			}
			
			rs.close();
		} catch (SQLException e) {
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
	
	public void initialize(URL location, ResourceBundle resources) {
		MemoryControl.page = 0;
		connect();
		data();
		disConnect();
		tID.setEditable(false);
		
		bSignOut.setOnAction(event->bSignOutAction(event));
		bCancel.setOnAction(event->bCancelAction(event));
		bRecord.setOnAction(event->bRecordAction(event));
		bEvent.setOnAction(event->bEventAction(event));
		bHome.setOnAction(event->bHomeAction(event));
		
		bLogOut.setOnAction(event->bLogOutAction(event));
		bLogEdit.setOnAction(event->bLogEditAction(event));
		bSignOutm.setOnAction(event->bSignOutmAction(event));
	}
	
	private void bLogOutAction(ActionEvent event) {
		LoginControl.loginID = "";
		shortCut("Login");
	}
	private void bSignOutmAction(ActionEvent event) {
		shortCut("Signout");
	}
	private void bLogEditAction(ActionEvent event) {
		shortCut("LogEdit");
	}
	
	private void bSignOutAction(ActionEvent event) {
		connect();
		boolean idCheck = false;
		try {
			String query = "SELECT * FROM member";

			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			while (rs.next()) {
				if (rs.getString("id").equals(LoginControl.loginID) && rs.getString("pw").equals(pPW.getText()) && rs.getString("name").equals(tName.getText()) && rs.getString("contact").equals(tcontact.getText())) {
					idCheck = true;
				}
			}
			rs.close();
			if (idCheck == true) {
				try {
					String queryReed = "SELECT * FROM record where id = '" + tID.getText() + "'";
					stmt = conn.createStatement();
					rs = stmt.executeQuery(queryReed);
					
					String path = System.getProperty("user.dir");
					String directory = path + "/src/note/image/" + tID.getText();

					while (rs.next()) {
						Path filePath = Paths.get(directory + "/" + rs.getString("image"));
						try {
							Files.delete(filePath);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					rs.close();
					
					Path directoryPath = Paths.get(directory);
					try {
						Files.delete(directoryPath);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				try {
					String queryMember = "delete from member where id = ?";
					PreparedStatement pstmt = conn.prepareStatement(queryMember);
					pstmt.setString(1, tID.getText());
					pstmt.executeUpdate();
					pstmt.close();
				} catch (SQLException ee) {
				}
				try {
					String queryRecord = "delete from record where id = ?";
					PreparedStatement pstmt = conn.prepareStatement(queryRecord);
					pstmt.setString(1, tID.getText());
					pstmt.executeUpdate();
					pstmt.close();
				} catch (SQLException ee) {
				}
				shortCut("Login");
				dialog("삭제하였습니다.");
			}
			else {
				dialog("정보를 다시 확인하세요.");
			}
			try {
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		disConnect();
	}
	
	private void bCancelAction(ActionEvent event) {
		shortCut("Home");
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
	