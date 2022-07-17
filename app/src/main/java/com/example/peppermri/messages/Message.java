package com.example.peppermri.messages;


import com.example.peppermri.crypto.Decryption;
import com.example.peppermri.crypto.Encryption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

public abstract class Message {
    protected MessageType type;
    private boolean value;
    private static Encryption encryption;
    private static Decryption decryption;
    public Message(MessageType type) {
        this.type = type;

        encryption = new Encryption();
        decryption = new Decryption();
    }

    /**
     * Creates and outputstream to send the message from the sender to the receiver
     * @param socket
     */
    public void send(Socket socket) {
        OutputStreamWriter out;
        try {
            String strEncryptedMessage;
            out = new OutputStreamWriter(socket.getOutputStream());
            strEncryptedMessage =encryption.encrypt(this.toString());
            out.write(this.toString() + "\n");
            out.flush();
        } catch (IOException e) {
            String err = e.getMessage();
        }
    }

    /**
     * The reader get all incoming messages and splits them into parts, to figure out what message types was sent to
     * the receiver, and splits the other parts of the message, for further usage of the receiving program
     * @param socket
     * @return
     */
    public static Message receive(Socket socket) {
        BufferedReader bfr;
        Message message = null;
        try {
            bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String msgText = bfr.readLine(); // Will wait here for complete line
            if (msgText != null) {

                String strDecryptedMessage = decryption.decrypt(msgText);

                // Parse message
                String[] parts = strDecryptedMessage.split("\\|");

                if (parts[0].equals(MessageType.Disconnect.toString())) {
                    message = new MessageSystem(parts[1]);
                    message.setType(MessageType.Disconnect);

                } else if (parts[0].equals(MessageType.Unsuccessful_LogIn.toString())) {
                    message = new MessageSystem(parts[1]);
                    message.setType(MessageType.Unsuccessful_LogIn);

                } else if (parts[0].equals(MessageType.Successful_LogIn.toString())) {
                    message = new MessageSystem(parts[1]);
                    message.setType(MessageType.Successful_LogIn);

                }else if (parts[0].equals(MessageType.LogOut.toString())) {
                    message.setType(MessageType.LogOut);

                } else if (parts[0].equals(MessageType.Disconnect.toString())) {
                    message = new MessageSystem(parts[1]);
                    message.setType(MessageType.Disconnect);

                } else if (parts[0].equals(MessageType.Patient.toString())) {
                    message = new MessageSystem(parts[1]);
                    message.setType(MessageType.Patient);

                } else if (parts[0].equals(MessageType.System.toString())) {
                    message = new MessageSystem(parts[1]);
                    message.setType(MessageType.System);

                } else if (parts[0].equals(MessageType.Test.toString())) {
                    message = new MessageSystem(parts[1]);
                    message.setType(MessageType.Test);

                }
            }
        } catch (IOException e) {
            String err = e.getMessage();
            //Controller.ClientIsConnected.set(false);
           // Controller.ServerIsStarted.set(false);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return message;
    }

    /**
     * Returns the type of the Message
     */
    public MessageType getType() {
        return this.type;
    }
    public void setType(MessageType msgType){
        this.type = msgType;
    }


    public boolean getBoolean() {
        return value;
    }

    public void setBoolean(boolean value) {
        this.value = value;
    }
}

