package note;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LogEditControl  implements Initializable {
	String url = "jdbc:mysql://localhost:3306/note?useUnicode=true&characterEncoding=utf8";
	String id = "root";
	String pass = "qwer";
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	PreparedStatement pstmt = null;
	
	@FXML private Button bRecord;
	@FXML private Button bEvent;
	@FXML private Button bHome;
	@FXML private TextField tID;
	@FXML private PasswordField pPW;
	@FXML private TextField tName;
	@FXML private TextField tcontact;
	@FXML private Button bLogEdit;
	@FXML private Button bCancel;
	@FXML private MenuButton menu;
	@FXML private MenuItem bLogOut;
	@FXML private MenuItem bLogEditm;
	@FXML private MenuItem bSignOut;
	
	private Stage primaryStage;	
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	
	String path = System.getProperty("user.dir");
	
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
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		MemoryControl.page = 0;
		bRecord.setOnAction(event->bRecordAction(event));
		bEvent.setOnAction(event->bEventAction(event));
		bHome.setOnAction(event->bHomeAction(event));
		bLogEdit.setOnAction(event->{
			try {
				bLogEditAction(event);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		bCancel.setOnAction(event->bCancelAction(event));
		
		bLogOut.setOnAction(event->bLogOutAction(event));
		bLogEditm.setOnAction(event->bLogEditmAction(event));
		bSignOut.setOnAction(event->bSignOutAction(event));
		
		connect();
		data();
		disConnect();
	}
	
	private void bLogOutAction(ActionEvent event) {
		LoginControl.loginID = "";
		shortCut("Login");
	}
	private void bSignOutAction(ActionEvent event) {
		shortCut("Signout");
	}
	private void bLogEditmAction(ActionEvent event) {
		shortCut("LogEdit");
	}

	
	private void bLogEditAction(ActionEvent event) throws IOException {
		connect();
		boolean idCheck = true;
		boolean check = false;
		if (tID.getText().equals("") == true || pPW.getText().equals("") == true || tName.getText().equals("") == true || tcontact.getText().equals("") == true) {
			dialog("정보를 모두 입력하세요.");
		}
		else {
			try {
				String query = "SELECT * from member";
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query);

				while (rs.next()) {
					if (tID.getText().equals(rs.getString("id"))) {
						if (tID.getText().equals(LoginControl.loginID) == false) {
							dialog("아이디가 중복됩니다.");
							idCheck = false;
						}
					}
				}
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (idCheck == true) {
				try {
					String query = "SELECT * FROM member";

					stmt = conn.createStatement();
					rs = stmt.executeQuery(query);

					while (rs.next()) {
						if (rs.getString("name").equals(tName.getText()) && rs.getString("contact").equals(tcontact.getText())) {
							check = true;
						}
					}
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if (check == true) {
					try {
						String query = "update member set id = ?, pw = ? where id = '" + LoginControl.loginID + "'";
						pstmt = conn.prepareStatement(query);

						pstmt.setString(1, tID.getText());
						pstmt.setString(2, pPW.getText());
						pstmt.executeUpdate();
						pstmt.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					try {
						String query = "update record set id = ? where id = '" + LoginControl.loginID + "'";
						pstmt = conn.prepareStatement(query);

						pstmt.setString(1, tID.getText());
						pstmt.executeUpdate();
						pstmt.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					Path sourceFolder = Paths.get(path + "/src/note/image/" + LoginControl.loginID);
					LoginControl.loginID = tID.getText();
					try {
						Files.move(sourceFolder, sourceFolder.resolveSibling(path + "/src/note/image/" + LoginControl.loginID), StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e) {
						e.printStackTrace();
					}
					shortCut("Home");
					dialog("수정하였습니다.");
				}
				else if (check == false) {
					dialog("정보를 다시 확인하세요.");
				}
			}
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
