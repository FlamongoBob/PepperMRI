package com.example.peppermri.messages;


import static com.example.peppermri.serverclient.ServerClient.logger;

import com.example.peppermri.serverclient.ServerClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Message {
    protected MessageType type;
    private boolean value;

    private static BufferedReader bfr;
    private static OutputStreamWriter out;

    private static DataInputStream dIn;
    private static DataOutputStream dOut;

    public Message(MessageType type) {
        this.type = type;
    }

    /**
     * Creates and Output Stream to send the message from the sender to the receiver
     *
     * @param socket
     */
    public void send(Socket socket) {
        try {
            dOut = new DataOutputStream(socket.getOutputStream());

            dOut.writeUTF(this.toString());
            dOut.flush();

        } catch (Exception e) {
            String err = e.getMessage();
        }
    }

    /**
     * The reader get all incoming messages and splits them into parts, to figure out what message types was sent to
     * the receiver, and splits the other parts of the message, for further usage of the receiving program
     *
     * @param socket
     * @return
     */
    public static Message receive(Socket socket) {
        Message message = null;
        try {
            dIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            if (socket != null && !socket.isClosed()) {

                while (!socket.isClosed()) {

                    String msgText;
                    try {

                        if (dIn == null) {
                            dIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                        }
                        msgText = dIn.readUTF(); // Will wait here for complete line

                    } catch (EOFException eofe) {
                        break;
                    }

                    if (msgText != null) {

                        //String strDecryptedMessage = decryption.decrypt(msgText);

                        // Parse message
                        String[] parts = msgText.split("\\|");

                        if (parts[0].equals(MessageType.Disconnect.toString())) {
                            message = new MessageSystem(parts[1]);
                            message.setType(MessageType.Disconnect);

                        } else if (parts[0].equals(MessageType.Login.toString())) {
                            message = new MessageLogin(parts[1]
                                    , parts[2]
                                    , MessageType.Login);
                            message.setType(MessageType.Login);

                        } else if (parts[0].equals(MessageType.Unsuccessful_LogIn.toString())) {
                            message = new MessageSystem(parts[1]);
                            message.setType(MessageType.Unsuccessful_LogIn);

                        } else if (parts[0].equals(MessageType.Successful_LogIn.toString())) {
                            message = new MessageSystem(parts[1]);
                            message.setType(MessageType.Successful_LogIn);

                        } else if (parts[0].equals(MessageType.LogOut.toString())) {
                            message.setType(MessageType.LogOut);

                        } else if (parts[0].equals(MessageType.Disconnect.toString())) {
                            message = new MessageSystem(parts[1]);
                            message.setType(MessageType.Disconnect);

                        } else if (parts[0].equals(MessageType.Patient.toString())) {
                            message = new MessageSystem(parts[1]);
                            message.setType(MessageType.Patient);

                        } else if (parts[0].equals(MessageType.Accept.toString())) {
                            message = new MessageSystem(
                                    parts[1]
                            );
                            message.setType(MessageType.Accept);

                        } else if (parts[0].equals(MessageType.Deny.toString())) {
                            message = new MessageSystem(
                                    parts[1]
                            );
                            message.setType(MessageType.Deny);

                        } else if (parts[0].equals(MessageType.System.toString())) {
                            message = new MessageSystem(parts[1]);
                            message.setType(MessageType.System);

                        } else if (parts[0].equals(MessageType.AllUser.toString())) {
                            message = new MessageSystem("");
                            message.setType(MessageType.AllUser);

                        } else if (parts[0].equals(MessageType.Test.toString())) {
                            message = new MessageSystem("");
                            message.setType(MessageType.Test);

                        } else if (parts[0].equals(MessageType.InsertUser.toString())) {
                            message = new MessageI(parts[1]
                                    , parts[2]
                                    , parts[3]
                                    , parts[4]
                                    , Integer.parseInt(parts[5])
                                    , parts[6]
                                    , parts[7]
                                    , Integer.parseInt(parts[8])
                            );

                        } else if (parts[0].equals(MessageType.UpdateUser.toString())) {
                            message = new MessageU(Integer.parseInt(parts[1])
                                    , parts[2]
                                    , parts[3]
                                    , parts[4]

                                    , Integer.parseInt(parts[5])
                                    , parts[6]

                                    , Integer.parseInt(parts[7])
                                    , parts[8]
                                    , parts[9]

                                    , Integer.parseInt(parts[10])

                                    , Integer.parseInt(parts[11])
                                    , Integer.parseInt(parts[12])
                            );

                        } else if (parts[0].equals(MessageType.DeleteUser.toString())) {
                            message = new MessageD(Integer.parseInt(parts[1])
                                    , Integer.parseInt(parts[2])
                                    , Integer.parseInt(parts[3])
                            );

                        }
                    } else {
                        break;
                    }
                    return message;
                }
            }
        } catch (Exception e) {

            String err = e.getMessage();
            err += "";
            //Controller.ClientIsConnected.set(false);
            // Controller.ServerIsStarted.set(false);
        }

        return message;
    }

    /**
     * Returns the type of the Message
     */
    public MessageType getType() {
        return this.type;
    }

    public void setType(MessageType msgType) {
        this.type = msgType;
    }


    public boolean getBoolean() {
        return value;
    }

    public void setBoolean(boolean value) {
        this.value = value;
    }
}

