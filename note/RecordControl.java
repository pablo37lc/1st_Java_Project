package note;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.FileChooser.ExtensionFilter;

public class RecordControl implements Initializable {
	@FXML private ImageView imageView;
	@FXML private Button bConfirm;
	@FXML private Button bRecord;
	@FXML private Button bCancel;
	@FXML private Button bEvent;
	@FXML private Button bHome;
	@FXML private TextField tTitle;
	@FXML private TextArea tMemo;
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

	String path = System.getProperty("user.dir");
	String imagePath = path + "/src/note/image/" + LoginControl.loginID + "/";
	String imgPath = "";
	private Stage primaryStage;	
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	FileChooser fileChooser = new FileChooser();
    File selectedFile;
    Path to;
    Path from;
	
    String datePick;
    Date date = new Date();
	SimpleDateFormat dateform = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat record = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");

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
	public void centerImage() {
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
		imageView.setOnMouseClicked(event->imageAction(event));
		bConfirm.setOnAction(event->{
			try {
				bConfirmAction(event);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		bCancel.setOnAction(event->bCancelAction(event));
		bRecord.setOnAction(event->bRecordAction(event));
		bEvent.setOnAction(event->bEventAction(event));
		bHome.setOnAction(event->bHomeAction(event));
		
		imageView.setImage(new Image("file:/" + path + "/src/note/image/Default.jpg"));
		centerImage();
		datePick = dateform.format(date);
		lDate.setText(datePick);
		
		datePicker.setOnAction(event->datePicker(event));
		
		bLogOut.setOnAction(event->bLogOutAction(event));
		bLogEdit.setOnAction(event->bLogEditAction(event));
		bSignOut.setOnAction(event->bSignOutAction(event));
	}

	private void datePicker(ActionEvent event) {
		datePick = datePicker.getValue().toString();
		lDate.setText(datePick);
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
	private void imageAction(MouseEvent event) {
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
		selectedFile = fileChooser.showOpenDialog(primaryStage);
		if (selectedFile != null) {
			imgPath = "file:/" + selectedFile.getPath();
			imageView.setImage(new Image(imgPath));
			centerImage();
		}
	}
	private void bConfirmAction(ActionEvent event) throws IOException {
		connect();
		File file = new File(path + "/src/note/image/" + LoginControl.loginID); 
		if(file.exists() == false) {
			if (file.mkdir() == true) { 
			}
		}
		String imageName = record.format(date) + "_" + selectedFile.getName(); 
		String recordImage = imagePath + imageName;
		File dest = new File(recordImage);
        try {
			Files.copy(selectedFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			String Query = "Insert into record values (default, ?, ?, ?, ?, ?)";
			pstmt = conn.prepareStatement(Query);

			pstmt.setString(1, LoginControl.loginID);
			pstmt.setString(2, tTitle.getText());
			pstmt.setString(3, imageName);
			pstmt.setString(4, tMemo.getText());
			pstmt.setString(5, datePick);
			pstmt.executeUpdate();
			pstmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		MemoryControl.select = imageName;
		shortCut("View");
		dialog("등록하였습니다.");
		disConnect();
	}
	private void bCancelAction(ActionEvent event) {
		imageView.setImage(new Image("file:/" + path + "/src/note/image/Default.jpg"));
		centerImage();
		tTitle.setText("");
		tMemo.setText("");
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
