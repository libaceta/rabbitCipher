package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.event.ActionEvent;

public class CipherController {

	public void cipher(ActionEvent event) {
		byte[] key = os2ip((byte)0x91, (byte)0x28, (byte)0x13, (byte)0x29, (byte)0x2E, (byte)0x3D, (byte)0x36, (byte)0xFE, (byte)0x3B, (byte)0xFC, (byte)0x62, (byte)0xF1, (byte)0xDC, (byte)0x51, (byte)0xC3, (byte)0xAC);
		byte[] msg = "Hello World!".getBytes();
		byte[] original = msg.clone();
		
		System.out.println("Mensaje original(bytes):	" + toBinaryString(original));
	    RabbitCipher cipher = new RabbitCipher();
	    cipher.setupKey(key);
	    byte[] cipherMessage = cipher.crypt(msg);
	    
	    System.out.println("Mensaje encriptado(bytes):	" + toBinaryString(cipherMessage));
	    
	    cipher.reset();
	    cipher.setupKey(key);
	    byte[] decipherMessage = cipher.crypt(cipherMessage);
	    
	    System.out.println("Mensaje desencriptado(bytes):	" + toBinaryString(decipherMessage));
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

}
