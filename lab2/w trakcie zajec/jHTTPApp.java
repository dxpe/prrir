import java.net.*;
import java.io.*;
import java.util.*;
class jHTTPSMulti extends Thread {
    private Socket socket = null;
    String getAnswer() {
        InetAddress adres;
        String name = "";
        String ip = "";
        try {
            adres = InetAddress.getLocalHost();
            name = adres.getHostName();
            ip = adres.getHostAddress();
        }
        catch (UnknownHostException ex) { System.err.println(ex); }
        String document = "<html>\r\n" +
                "<style>\n" +
                "body {\n" +
                "  background-color: linen;\n" +
                "  max-width: 500px;\n" +
                "  margin: auto;\n" +
                " text-align: center;\n" +
                "}\n" +
                "\n" +
                "h1 {\n" +
                "  color: maroon;\n" +
                "  margin-left: 40px;\n" +
                "}" +
                ".tg  {border-collapse:collapse;border-spacing:0;margin:auto;}\n" +
                ".tg td{border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;\n" +
                "  overflow:hidden;padding:10px 5px;word-break:normal;}\n" +
                ".tg th{border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;\n" +
                "  font-weight:normal;overflow:hidden;padding:10px 5px;word-break:normal;}\n" +
                ".tg .tg-8n61{color:red;text-align:left;text-decoration:underline;vertical-align:top}\n" +
                ".tg .tg-7803{color:red;text-align:left;vertical-align:top}\n" +
                ".tg .tg-0lax{text-align:left;vertical-align:top}" +
                "</style>\n" +
                "<body><br>\r\n" +
                "<h2><font color=red>jHTTPApp demo document\r\n" +
                "</font></h2>\r\n" +
                "<h3>Serwer na watkach</h3><hr>\r\n" +
                "<table class=\"tg\">\n" +
                "<thead>\n" +
                "  <tr>\n" +
                "    <th class=\"tg-8n61\">Data:</th>\n" +
                "    <th class=\"tg-0lax\"><b>" + new Date() + " <b></th>\n" +
                "  </tr>\n" +
                "</thead>\n" +
                "<tbody>\n" +
                "  <tr>\n" +
                "    <td class=\"tg-7803\">Nazwa hosta:</td>\n" +
                "    <td class=\"tg-0lax\"><b>" + name + "</b></td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td class=\"tg-7803\">IP hosta:</td>\n" +
                "    <td class=\"tg-0lax\"><b>" + ip + "</b></td>\n" +
                "  </tr>\n" +
                "</tbody>\n" +
                "</table>" +
                "<hr>\r\n" +
                "</body>\r\n" +
                "</html>\r\n";
        String header = "HTTP/1.1 200 OK\r\n" +
                "Server: jHTTPServer ver 1.1\r\n" +
                "Last-Modified: Fri, 28 Jul 2000 07:58:55 GMT\r\n" +
                "Content-Length: " + document.length() + "\r\n" +
                "Connection: close\r\n" +
                "Content-Type: text/html; charset=iso-8859-2";
        return header + "\r\n\r\n" + document;
    }
    public jHTTPSMulti(Socket socket){
        System.out.println("Nowy obiekt jHTTPSMulti...");
        this.socket = socket;
        start();
    }
    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            System.out.println("---------------- Pierwsza linia zapytania ----------------");
            System.out.println(in.readLine());
            System.out.println("---------------- Wysylam odpowiedz -----------------------");
            System.out.println(getAnswer());
            System.out.println("---------------- Koniec odpowiedzi -----------------------");
            out.println(getAnswer());
            out.flush();
        } catch (IOException e) {
            System.out.println("Blad IO danych!");
        }
        finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.out.println("Blad zamkniecia gniazda!");
            }
        }
    }
}
public class jHTTPApp {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(80);
        try {
            while (true) {
                Socket socket = server.accept();
                new jHTTPSMulti(socket);
            }
        }
        finally { server.close();}
    }
}
