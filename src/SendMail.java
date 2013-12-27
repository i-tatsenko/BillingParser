import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by docent on 26.12.13.
 */
public class SendMail {

    private final int ERROR_SMTP = 221;

    private final String SERVER;
    private String HELO = "HELO ";
    private int PORT = 25;
    private String SENDER = "MAIL FROM:";
    private String RCPT = "RCPT TO:";
    private PrintStream out;
    private DataInputStream in;

    public SendMail(String server, String rcpt, String sender) {
        try {
            HELO += InetAddress.getLocalHost().getAddress();
        }catch (IOException e) {
            System.err.println(e);
            HELO += "name.localhost";
        }
        SERVER = server;
        RCPT += "<" + rcpt + ">";
        SENDER += "<" + sender + ">";
    }

    public SendMail(String server, String rcpt) throws Exception{
        this(server, rcpt, "name@localhost");
    }

    public void setSENDER(String SENDER) {
        this.SENDER = SENDER;
    }

    public void setRCPT(String RCPT) {
        this.RCPT = RCPT;
    }

    public boolean sendMessage(String msg, String subj) {
        if (!connectToSmtp()) {
            System.out.println("Can't connect to server!");
            return false;
        }
        try {
            sendData(HELO);
            sendData(SENDER);
            sendData(RCPT);
            sendData("DATA");
            sendData("subject:" + subj + "\n" + msg + "\r\n.\r\n");
        }catch (IOException e) {
            System.err.print(e);
            return false;
        }catch (ServerFailedException e) {
            e.printException();
        }
        return true;
    }

    private boolean sendData(String data) throws IOException, ServerFailedException {
        out.println(data);
        out.flush();
        System.out.println("Sending: " + data);
        if (!isOk(in.readLine())) throw new ServerFailedException("Error in dialog sending: " + data);


        return true;
    }

    public boolean sendMessage(String msg) {
        return sendMessage(msg, null);
    }

    private boolean connectToSmtp() {
        try{
            Socket conn = new Socket(SERVER, PORT);
            System.out.println("Connecting to server: " + new DataInputStream(conn.getInputStream()).readLine());
            out = new PrintStream(conn.getOutputStream());
            in = new DataInputStream(conn.getInputStream());
            return true;

        }catch (IOException e) {
            System.err.print(e);
            return false;
        }
    }

    private boolean isOk(String answer) {
        System.out.println("isOk block: " + answer);
        int ansCode = Integer.valueOf(answer.substring(0,3));
        if (ansCode == ERROR_SMTP)return false;
        return true;
    }
}