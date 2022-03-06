package application;
	
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;

import controller.ConnectionController;
import controller.Controller;
import controller.MailboxController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import mailing.Mailsendreceivetest;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;


public class Main extends Application {
	
	private Stage primaryStage;
	private VBox mailboxLayout;
	private VBox connectionLayout;
	private BorderPane rootLayout;

	private User user;

	//emails

	private Properties receiveProperties;
	private Properties sendProperties;
	
	@Override
	public void start(Stage primaryStage) {
		
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Secure Mail");

		initProperties();
		
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("application.css")).toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

			initRootLayout();

			showConnectionView();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	private void initProperties(){
		receiveProperties = new Properties();

		receiveProperties.put("mail.imap.host", "imap.gmail.com");
		receiveProperties.put("mail.imap.port", "993");
		receiveProperties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		receiveProperties.setProperty("mail.imap.socketFactory.fallback", "false");
		receiveProperties.setProperty("mail.imap.socketFactory.port", "993");

		sendProperties = new Properties();

		sendProperties.put("mail.smtp.host", "smtp.gmail.com");
		sendProperties.put("mail.smtp.socketFactory.port", "465");
		sendProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		sendProperties.put("mail.smtp.auth", "true");
		sendProperties.put("mail.smtp.port", "465");
	}
	
	private void initRootLayout() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("../view/RootLayout.fxml"));
		
		try {
			rootLayout = (BorderPane) loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);		
		primaryStage.show();	
	}
	
	public void showConnectionView() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("../view/Connection.fxml"));
		
		Controller controller = new ConnectionController();
		loader.setController(controller);
		controller.setMain(this);
		
		try {
			connectionLayout = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		rootLayout.setCenter(connectionLayout);
	}
	
	public void showMailboxView() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("../view/Mailbox.fxml"));
		
		Controller controller = new MailboxController();
		loader.setController(controller);
		controller.setMain(this);
		
		try {
			mailboxLayout = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		rootLayout.setCenter(mailboxLayout);
	}

	public boolean setUser(User user) {
		this.user = user;

		Session session = Session.getDefaultInstance(receiveProperties);

		try {
			// connects to the message store imap or pop3
			//     Store store = session.getStore("pop3");
			Store store = session.getStore("imap");

			store.connect(user.getEmail(), user.getPassword());

			store.close();

		} catch (NoSuchProviderException ex) {
			System.out.println("No provider for imap.");
			ex.printStackTrace();
		} catch (MessagingException ex) {
			System.out.println("Could not connect to the message store");

			return false;
		}
		return true;
	}

	public User getUser() {
		return user;
	}
}
