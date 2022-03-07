package application;
	
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import controller.ConnectionController;
import controller.Controller;
import controller.MailboxController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import javax.mail.*;

public class Main extends Application {
	
	private Stage primaryStage;
	private VBox mailboxLayout;
	private VBox connectionLayout;
	private VBox mailLayout;
	private BorderPane rootLayout;

	private User user;

	//emails

	private Properties receiveProperties;
	private Properties sendProperties;
	private Store store;
	private Folder folderInbox;

	private Message[] emails;

	private boolean connected = false;
	
	@Override
	public void start(Stage primaryStage) {
		
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Secure Mail");

		initProperties();
		
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

			initRootLayout();

			showConnectionView();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() throws MessagingException {
		if (store != null){
			folderInbox.close(false);
			store.close();
			connected = false;
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
	
	public void showMailView() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("../view/Mail.fxml"));
		
		Controller controller = new MailboxController();
		loader.setController(controller);
		controller.setMain(this);
		
		try {
			mailLayout = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		rootLayout.setCenter(mailboxLayout);
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

	public boolean connectAs(User user) {
		this.user = new User("cryptoav.tp@gmail.com","vivelacrypto");

		Session session = Session.getDefaultInstance(receiveProperties);

		try {
			// connects to the message store imap or pop3
			//     Store store = session.getStore("pop3");
			store = session.getStore("imap");

			store.connect(this.user.getEmail(), this.user.getPassword());

			store.close();

		} catch (NoSuchProviderException ex) {
			System.out.println("No provider for imap.");
			ex.printStackTrace();

			return false;
		} catch (MessagingException ex) {
			System.out.println("Could not connect to the message store");

			return false;
		}

		connected = true;

		return true;
	}

	public boolean downloadMails(){
		if (connected){

			Session session = Session.getDefaultInstance(receiveProperties);

			try {
				store = session.getStore("imap");

				store.connect(user.getEmail(), user.getPassword());
				// opens the inbox folder
				folderInbox = store.getFolder("INBOX");
				folderInbox.open(Folder.READ_ONLY);
				// fetches new messages from server
				emails = folderInbox.getMessages();

			} catch (NoSuchProviderException ex) {
				System.out.println("No provider for imap.");
				ex.printStackTrace();
				return false;
			} catch (MessagingException ex) {
				System.out.println("Could not connect to the message store");
				ex.printStackTrace();
				return false;
			}
		}

		return true;
	}

	public User getUser() {
		return user;
	}

	public Message[] getEmails() {
		return emails;
	}
}
