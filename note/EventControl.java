package note;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class EventControl implements Initializable {
	@FXML private Button bRecord;
	@FXML private Button bHome;
	@FXML private Button bMemory;
	@FXML private Button bCancel;
	@FXML private Button bSearch;
	@FXML private TextField tSearch;
	@FXML private ChoiceBox cSearch;
	@FXML private TableView<EventList> tEvent;
	@FXML private TableColumn<EventList, String> tTitle;
	@FXML private TableColumn<EventList, String> tNote;
	@FXML private TableColumn<EventList, String> tDate;
	@FXML private MenuButton menu;
	@FXML private MenuItem bLogOut;
	@FXML private MenuItem bLogEdit;
	@FXML private MenuItem bSignOut;

	String url = "jdbc:mysql://localhost:3306/note?useUnicode=true&characterEncoding=utf8";
	String id = "root";
	String pass = "qwer";
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	PreparedStatement pstmt = null;
	
	String note;

	SimpleDateFormat dateform = new SimpleDateFormat("yyyy-MM-dd");
	
	ObservableList<EventList> myList = FXCollections.observableArrayList();

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
		tEvent.getItems().clear();
		try {
			String query = "SELECT * FROM record where id = '" + LoginControl.loginID + "' and " + cSearch.getSelectionModel().getSelectedItem().toString() + " like '%" + tSearch.getText() + "%' order by date DESC";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				note = rs.getString("note").replace("\n", ", ");;
				if (note.length() > 15) {
					note = note.substring(0, 15) + "...";
				}
				myList.add( new EventList( new SimpleStringProperty(rs.getString("title")),
										   new SimpleStringProperty(note),
										   new SimpleStringProperty(dateform.format(rs.getDate("date"))),
										   new SimpleStringProperty(rs.getString("image"))
									  )
						  );
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		tTitle.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
		tNote.setCellValueFactory(cellData -> cellData.getValue().noteProperty());
		tDate.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
		tEvent.setItems(myList);
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
	
	public void initialize(URL location, ResourceBundle resources) {
		MemoryControl.page = 0;
		connect();
		try {
			String query = "SELECT * FROM record where id = '" + LoginControl.loginID + "' order by date DESC";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				note = rs.getString("note").replace("\n", ", ");
				if (note.length() > 15) {
					note = note.substring(0, 15) + "...";
				}
				myList.add( new EventList( new SimpleStringProperty(rs.getString("title")),
									    new SimpleStringProperty(note),
									    new SimpleStringProperty(dateform.format(rs.getDate("date"))),
									    new SimpleStringProperty(rs.getString("image"))
									  )
						  );
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		tTitle.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
		tNote.setCellValueFactory(cellData -> cellData.getValue().noteProperty());
		tDate.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
		tEvent.setItems(myList);
		disConnect();
		
		bMemory.setOnAction(event->bMemoryAction(event));
		tEvent.setOnMouseClicked(event->tEventAction(event));
		bRecord.setOnAction(event->bRecordAction(event));
		bHome.setOnAction(event->bHomeAction(event));
		bSearch.setOnAction(event->bSearchAction(event));
		bLogOut.setOnAction(event->bLogOutAction(event));
		bLogEdit.setOnAction(event->bLogEditAction(event));
		bSignOut.setOnAction(event->bSignOutAction(event));
		
		cSearch.getItems().addAll("title", "note", "date");
		cSearch.setValue("title");
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
	private void bSearchAction(ActionEvent event) {
		connect();
		data();
		disConnect();
	}
	private void tEventAction(MouseEvent event) {
		if (tEvent.getSelectionModel().getSelectedItem() != null) {
			MemoryControl.select = tEvent.getSelectionModel().getSelectedItem().getImage();
			shortCut("View");
		}
	}
	private void bMemoryAction(ActionEvent event) {
		shortCut("Memory");
	}
	private void bRecordAction(ActionEvent event) {
		shortCut("Record");
	}
	private void bHomeAction(ActionEvent event) {
		shortCut("Home");
	}
}
