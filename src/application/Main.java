package application;
	
import java.io.*;
import java.net.URL;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import controller.*;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import server.HttpServeur;

import javax.mail.*;

public class Main extends Application {
	
	private Stage primaryStage;
	private VBox mailboxLayout;
	private VBox connectionLayout;
	private VBox mailLayout;
	private VBox sendMailLayout;
	private BorderPane rootLayout;

	private User user;

	private HashMap<String, byte[]> registeredUsersSalts;

	private  Pairing pairing = PairingFactory.getPairing("IBE/a.properties");;

	private ServerConfig serverConfig;

	//emails

	private Properties receiveProperties;
	private Properties sendProperties;
	private Folder folderInbox;

	private Message[] emails;

	private boolean connected = false;

	private static Main singleton;

	public static Main getInstance(){
		return singleton;
	}
	
	@Override
	public void start(Stage primaryStage) throws IOException, ClassNotFoundException {

		if (singleton == null)
			singleton = this;
		
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Secure Mail");

		serverConfig = new ServerConfig(8080, "19");

		loadUsers();

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

	private void loadUsers() throws IOException, ClassNotFoundException {
		URL url = HttpServeur.class.getResource("registeredUsers");
		File save = new File(url.getPath());

		FileInputStream fileInputStream = new FileInputStream(save);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

		registeredUsersSalts = (HashMap<String, byte[]>) objectInputStream.readObject();
	}

	@Override
	public void stop() throws MessagingException {
		folderInbox.close();
		connected = false;
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

	public void showSendMail() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("../view/SendMail.fxml"));

			Controller controller = new SendMailController();
			controller.setMain(this);

			loader.setController(controller);

			Scene scene = new Scene(loader.load(), 900, 600);
			Stage stage = new Stage();
			stage.setTitle("Write a mail");
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			Logger logger = Logger.getLogger(getClass().getName());
			logger.log(Level.SEVERE, "Failed to create new Window.", e);
		}
	}

	public boolean connectAs(User user)  {
		this.user = user;

		Session session = Session.getDefaultInstance(receiveProperties);

		try {
			// connects to the message store imap or pop3
			//     Store store = session.getStore("pop3");
			Store store = session.getStore("imap");

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

		if (!registeredUsersSalts.containsKey(user.getEmail())){
			// Generate salt
			SecureRandom secureRandom = new SecureRandom();
			byte salt[] = new byte[20];
			secureRandom.nextBytes(salt);

			// Add salt to list of saved users
			registeredUsersSalts.put(user.getEmail(),salt);
			this.user.setSalt(salt);

			URL url = HttpServeur.class.getResource("registeredUsers");
			File save = new File(url.getPath());
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(save,false);
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
				objectOutputStream.writeObject(registeredUsersSalts);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Could not registered user and salt");
				return false;
			}
		}else {
			this.user.setSalt(registeredUsersSalts.get(user.getEmail()));
		}

		connected = true;

		return true;
	}

	public boolean downloadMails(){
		if (connected){

			Session session = Session.getDefaultInstance(receiveProperties);

			try {
				Store store = session.getStore("imap");

				store.connect(user.getEmail(), user.getPassword());

				if (folderInbox!= null && folderInbox.isOpen())
					folderInbox.close();
				folderInbox = store.getFolder("INBOX");
				folderInbox.open(Folder.READ_ONLY);

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

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public Properties getSendProperties() {
		return sendProperties;
	}

	public Pairing getPairing() {
		return pairing;
	}

	public ServerConfig getServerConfig() {
		return serverConfig;
	}
}
