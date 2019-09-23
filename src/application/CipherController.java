package application;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class CipherController {

	private byte[] inputFile;
	private String inputPath;
	private int headerSpace;
	
	@FXML
	private TextField txtInputPath;
	
	@FXML
	private Label lbInfo;
	
	private Desktop desktop = Desktop.getDesktop();

	public void importFile(ActionEvent event) {

		final GridPane inputGridPane = new GridPane();
		
		final Pane rootGroup = new VBox(12);
		rootGroup.getChildren().addAll(inputGridPane);
		rootGroup.setPadding(new Insets(12, 12, 12, 12));

		Stage stage = new Stage();
		stage.setScene(new Scene(rootGroup));
		
		final FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(stage);
		inputPath = file.getPath();
		txtInputPath.setText(file.getPath());
		
		lbInfo.setText("Archivo cargado");
		
		try {
			inputFile = Files.readAllBytes(file.toPath());
			System.out.println("Input File: " + file.getPath());
			String extension = file.getPath().substring(file.getPath().lastIndexOf("."));
			switch(extension) {
				case ".txt": headerSpace = 0; break;
				case ".bmp": headerSpace = 54; break;
				default: headerSpace = 0;
			}
			
			//System.out.println(toBinaryString(inputFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void openFile(File file) {
		try {
			desktop.open(file);
		} catch (IOException ex) {
			Logger.getLogger(CipherController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void cipher(ActionEvent event) {
		//cryptDecryptTest();
		lbInfo.setText("");
		
		if(inputFile == null) {
			return; //msj error
		}
		
		byte[] key = os2ip((byte)0x91, (byte)0x28, (byte)0x13, (byte)0x29, (byte)0x2E, (byte)0x3D, (byte)0x36, (byte)0xFE, (byte)0x3B, (byte)0xFC, (byte)0x62, (byte)0xF1, (byte)0xDC, (byte)0x51, (byte)0xC3, (byte)0xAC);
		//System.out.println("Key (bytes - bits):");
		//System.out.println(toBinaryString(key));
		
		RabbitCipher cipher = new RabbitCipher();
		cipher.setupKey(key);
		byte[] cipherMessage = cipher.crypt(inputFile, headerSpace);
		//System.out.println("Mensaje encriptado(bytes):	" + toBinaryString(cipherMessage));
		
		
		try {
			File file = new File(inputPath);
			OutputStream os;
			os = new FileOutputStream(file);
			os.write(inputFile);
	        os.close();
	        lbInfo.setText("Archivo encriptado");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			lbInfo.setText("Error generando nuevo archivo");
		} catch (IOException e) {
			e.printStackTrace();
			lbInfo.setText("Error generando nuevo archivo");
		}
        
		
		
	}

	private byte[] os2ip(byte ... bytes) {
		List<Byte> test = new ArrayList<Byte>();
		for(byte b : bytes) {
			test.add(b);
		}
		Collections.reverse(test);
		byte[] result = new byte[bytes.length];
		for(int i=0; i<bytes.length; i++) {
			result[i] = test.get(i);
		}

		return result;
	}

	
	
	public String toBinaryString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		StringBuilder byteString = new StringBuilder();
		for(byte b : bytes) {
			byteString.append(b + " ");
			String s = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
			sb.append(s);
		}
		System.out.println(byteString.toString());
		return sb.toString();
	}

	private void cryptDecryptTest() {
		byte[] key = os2ip((byte)0x91, (byte)0x28, (byte)0x13, (byte)0x29, (byte)0x2E, (byte)0x3D, (byte)0x36, (byte)0xFE, (byte)0x3B, (byte)0xFC, (byte)0x62, (byte)0xF1, (byte)0xDC, (byte)0x51, (byte)0xC3, (byte)0xAC);
		byte[] msg = "Hello World!".getBytes();
		byte[] original = msg.clone();

		System.out.println("Mensaje original(bytes):	" + toBinaryString(original));
		RabbitCipher cipher = new RabbitCipher();
		cipher.setupKey(key);
		byte[] cipherMessage = cipher.crypt(msg, 0);

		System.out.println("Mensaje encriptado(bytes):	" + toBinaryString(cipherMessage));

		cipher.reset();
		cipher.setupKey(key);
		byte[] decipherMessage = cipher.crypt(cipherMessage, 0);

		System.out.println("Mensaje desencriptado(bytes):	" + toBinaryString(decipherMessage));
	}
}
