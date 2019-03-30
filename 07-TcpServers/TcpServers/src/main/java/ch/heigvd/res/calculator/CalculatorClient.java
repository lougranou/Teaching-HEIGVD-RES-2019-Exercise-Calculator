package ch.heigvd.res.calculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


    /**
     * This class implements a simple client for our custom presence protocol.
     * When the client connects to a server, a thread is started to listen for
     * notifications sent by the server.
     *
     * @author Olivier Liechti
     */
    public class CalculatorClient {

        final static Logger LOG = Logger.getLogger(CalculatorClient.class.getName());

        Socket clientSocket;
        BufferedReader in;
        PrintWriter out;
        boolean connected = false;


        /**
         * This inner class implements the Runnable interface, so that the run()
         * method can execute on its own thread. This method reads data sent from the
         * server, line by line, until the connection is closed or lost.
         */
        class NotificationListener implements Runnable {

            @Override
            public void run() {
                String notification;
                try {
                    while ((connected && (notification = in.readLine()) != null)) {


                        LOG.log(Level.INFO, "Server notification for {1}: {0}", new Object[]{notification});
                    }
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "Connection problem in client used by {1}: {0}", new Object[]{e.getMessage()});
                    connected = false;
                } finally {
                    cleanup();
                }
            }
        }

        /**
         * This inner class implements the Runnable interface, so that the run()
         * method can execute on its own thread. This method reads data sent from the
         * server, line by line, until the connection is closed or lost.
         */
        class CalculWriter implements Runnable {
            Scanner sc;
            CalculWriter(){
                this.sc = new Scanner(System.in);
            }
            @Override
            public void run() {
                String calcul;
            }
        }
        /**
         * This method is used to connect to the server and to inform the server that
         * the user "behind" the client has a name (in other words, the HELLO command
         * is issued after successful connection).
         *
         * @param serverAddress the IP address used by the Presence Server
         * @param serverPort the port used by the Presence Server
         */
        public void connect(String serverAddress, int serverPort) {
            String line;
            Scanner sc = new Scanner(System.in);
            try {
                clientSocket = new Socket(serverAddress, serverPort);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream());
                connected = true;
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Unable to connect to server: {0}", e.getMessage());
                cleanup();
                return;
            }


            while (connected) {
                try {


                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                    System.out.println("before scanner");
                    line = sc.nextLine();
                    System.out.println("send : " + line);
                    out.println(sc.nextLine());
                    out.flush();

                } catch (IOException ex) {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ex1) {
                            LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                        }
                    }
                    if (out != null) {
                        out.close();
                    }
                    if (clientSocket != null) {
                        try {
                            clientSocket.close();
                        } catch (IOException ex1) {
                            LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                        }
                    }
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }


            }
        }

        public void disconnect() {
            LOG.log(Level.INFO, "{0} has requested to be disconnected.");
            connected = false;
            out.println("BYE");
            cleanup();
        }

        private void cleanup() {

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }

            if (out != null) {
                out.close();
            }

            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        public static void main(String[] args) {
            CalculatorClient c1 = new CalculatorClient();
            c1.connect("localhost", Protocol.DEFAULT_PORT);
        }
    }


