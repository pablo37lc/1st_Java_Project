package note;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class HomeControl implements Initializable{
	
	@FXML private Button bRecord;
	@FXML private Button bMemory;
	@FXML private Button bEvent;
	@FXML private Button bEvent1;
	@FXML private Button bRecord1;
	@FXML private Button bHome;
	@FXML private MenuButton menu;
	@FXML private MenuItem bLogOut;
	@FXML private MenuItem bLogEdit;
	@FXML private MenuItem bSignOut;
	
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		MemoryControl.page = 0;
		bRecord.setOnAction(event->bRecordAction(event));
		bMemory.setOnAction(event->bMemoryAction(event));
		bEvent.setOnAction(event->bEventAction(event));
		bEvent1.setOnAction(event->bMemoryAction(event));
		bRecord1.setOnAction(event->bRecordAction(event));
		bHome.setOnAction(event->bHomeAction(event));
		
		bLogOut.setOnAction(event->bLogOutAction(event));
		bLogEdit.setOnAction(event->bLogEditAction(event));
		bSignOut.setOnAction(event->bSignOutAction(event));
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
	private void bMemoryAction(ActionEvent event) {
		shortCut("Memory");
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
