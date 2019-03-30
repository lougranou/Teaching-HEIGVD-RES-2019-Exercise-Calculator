package ch.heigvd.res.calculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements a multi-threaded TCP server. It is able to interact
 * with several clients at the time, as well as to continue listening for
 * connection requests.
 *
 * @author Olivier Liechti
 */
public class MultiThreadedServer {

	final static Logger LOG = Logger.getLogger(MultiThreadedServer.class.getName());


	int port;

	/**
	 * Constructor
	 *
	 * @param port the port to listen on
	 */
	public MultiThreadedServer(int port) {
		this.port = port;
	}

	/**
	 * This method initiates the process. The server creates a socket and binds it
	 * to the previously specified port. It then waits for clients in a infinite
	 * loop. When a client arrives, the server will read its input line by line
	 * and send back the data converted to uppercase. This will continue until the
	 * client sends the "BYE" command.
	 */
	public void serveClients() {
		LOG.info("Starting the Receptionist Worker on a new thread...");
		new Thread(new ReceptionistWorker()).start();
	}

	/**
	 * This inner class implements the behavior of the "receptionist", whose
	 * responsibility is to listen for incoming connection requests. As soon as a
	 * new client has arrived, the receptionist delegates the processing to a
	 * "servant" who will execute on its own thread.
	 */
	private class ReceptionistWorker implements Runnable {

		@Override
		public void run() {
			ServerSocket serverSocket;

			try {
				serverSocket = new ServerSocket(port);
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, null, ex);
				return;
			}

			while (true) {
				LOG.log(Level.INFO, "Waiting (blocking) for a new client on port {0}", port);
				try {
					Socket clientSocket = serverSocket.accept();
					LOG.info("A new client has arrived. Starting a new thread and delegating work to a new servant...");
					new Thread(new ServantWorker(clientSocket)).start();
				} catch (IOException ex) {
					Logger.getLogger(MultiThreadedServer.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

		}

	/**
	 * This inner class implements the behavior of the "servants", whose
	 * responsibility is to take care of clients once they have connected. This
	 * is where we implement the application protocol logic, i.e. where we read
	 * data sent by the client and where we generate the responses.
	 */
		private class ServantWorker implements Runnable {

			Socket clientSocket;
			BufferedReader in = null;
			PrintWriter out = null;
			final  char VALID_OPERATOR[] = {'x', '+'};
			final  String DELIMITER = " ";
			String[] tokens;
			char currentOperator;
			private boolean isValidOperator = false;

			public ServantWorker(Socket clientSocket) {
				try {
					this.clientSocket = clientSocket;
					in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					out = new PrintWriter(clientSocket.getOutputStream());
				} catch (IOException ex) {
					Logger.getLogger(MultiThreadedServer.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

			@Override
			public void run() {
				String line;
				boolean shouldRun = true;

				out.println("Welcome to the Calculator Server.\nSend me your calculus <operand_1> <operator> <operand_2> and conclude with the BYE command.");
				out.flush();
				try {




					LOG.info("Reading until client sends BYE or closes the connection...");
					while ((shouldRun) && (line = in.readLine()) != null) {
						if (line.equalsIgnoreCase("bye")) {
							out.println("See you soon !");
							out.flush();
							shouldRun = false;
						}

						tokens = line.split(DELIMITER);

						if(shouldRun) {

							/*
							 * if tokens must be of length three for a valid calcul
							 */
						if(tokens.length != 3
						|| !tokens[0].matches("[0-9]+") || !tokens[2].matches("[0-9]+")){
							wrongCalcul(out);
							continue;
						}

							/**
							 * tokens[1] must be an valid operator
							 */
						for(char c : VALID_OPERATOR){
							if(tokens[1].charAt(0) == c){
								this.currentOperator = tokens[1].charAt(0);
								this.isValidOperator = true;
								break;
							}
						}

						if(!isValidOperator){
							wrongCalcul(out);
							continue;
						}

							/**
							 * calcul and send result
							 */

							int operand_1 = Integer.parseInt(tokens[0]), operand_2 = Integer.parseInt(tokens[0]);
							switch (this.currentOperator){
								case '+':
									writeResult(out, operand_1 + operand_2);
									break;
								case 'x':
									writeResult(out, operand_1 * operand_2);
									break;

									default:
										wrongCalcul(out);


							}
							this.isValidOperator = false;
						}
					}



					LOG.info("Cleaning up resources...");
					clientSocket.close();
					in.close();
					out.close();

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

			private void wrongCalcul(PrintWriter out){
				out.println("Wrong calcul, usage :  <operand_1> <operator> <operand_2>");
				out.flush();
			}

		private void writeResult(PrintWriter out, int result){
			out.println("> " + result);
			out.flush();
		}
		}
	}
}
