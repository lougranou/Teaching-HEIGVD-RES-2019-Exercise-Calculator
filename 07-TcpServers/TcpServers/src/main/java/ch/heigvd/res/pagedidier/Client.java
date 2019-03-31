package ch.heigvd.res.pagedidier;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;

import static java.lang.System.exit;
import java.net.Socket;
import java.util.Scanner;
import java.io.PrintWriter;


public class Client {
    static PrintWriter out;
    static BufferedReader in;
    public static void main(String ... args) throws IOException {
        if(args.length != 2){
            System.out.println("Usage: java Client <address> <port>");
            exit(0);
        }

        String serverAddress = args[0];
        int portNumber= Integer.parseInt(args[1]);

        Socket socket = new Socket(serverAddress, portNumber);
        out = new PrintWriter(socket.getOutputStream());
        out.flush();

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String line = in.readLine();
        System.out.println(line);
        Scanner scan= new Scanner(System.in);
        while ((line = scan.nextLine()) != null && !line.equals("bye")){
            out.println(line);
            out.flush();
            System.out.println(in.readLine());
        }
        out.write("bye");
        out.flush();
    }
}
