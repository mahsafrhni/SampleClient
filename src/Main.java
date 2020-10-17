import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    final static int PORT = 80;

    public static void main(String[] args) {
        Pattern UrlRegex = Pattern.compile("(?i)(http://)?([-a-zA-Z0-9@:%._\\+~#=]{1,256}" +
                "\\.[a-zA-Z0-9()]{1,6}\\b)([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");
        List<String> methods = List.of("GET", "POST", "PATCH", "PUT", "DELETE");
        String input, studentId = "";
        String error403 = "403 Forbidden";
        String error404 = "404 Not Found";
        String error500 = "500 Internal Server Error";
        Scanner scanner = new Scanner(System.in);  //scanner
        while (true) {
            System.out.println("please enter a command or url:");
            input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("set-student-id-header")) {
                System.out.println("please enter student id");
                studentId = scanner.nextLine().toLowerCase().trim();
                continue;
            }
            if (input.equals("remove-student-id-header")) {
                studentId = "";
                continue;
            }
            if (input.equals("exit")) {
                break;
            }
            Matcher result = UrlRegex.matcher(input);
            if (result.matches()) {
                System.out.println("please Enter http method:");
                System.out.println("GET for get method, \n" +
                        "POST for post method,\n" +
                        "PUT for put method,\n" +
                        "PATCH for patch method,\n" +
                        "DELETE for delete method.\n");
                String inputMethod = scanner.nextLine().trim().toUpperCase();
                if (!methods.contains(inputMethod)) {
                    System.out.println("Please choose one of GET, POST, PUT, PATCH, DELETE!");
                    continue;
                }
                System.out.println("Method matches;");
                String hostname = result.group(2);
                String resource = result.group(3);
                if (resource.isEmpty()) {
                    resource = "/";
                }
                try {
                    Socket socket = new Socket(hostname, PORT);
                    System.out.println(socket.isConnected());
                    OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
                    String request = makeRequestBody(resource, inputMethod, studentId);
                    out.write(request);
                    out.flush();
                    Scanner ResponseScanner = new Scanner(socket.getInputStream());
                    StringBuilder Response = new StringBuilder(); ///BODY
                    while (ResponseScanner.hasNextLine()) {
                        Response.append(ResponseScanner.nextLine()).append("\n");
                    }
                    System.out.println(Response.toString());
                    ////check MIME types
                    if (Response.toString().contains("text/html")) {
                        // if (inputMethod.equals("GET")) {
                        // String[] arrSplit = inputSb.toString().split("<!DOCTYPE");
                        File output = new File("test.html");
                        FileWriter writer = new FileWriter(output);
                        // writer.write("<!DOCTYPE");
                        // writer.write(arrSplit[1]);
                        writer.write(Response.toString());
                        writer.flush();
                        writer.close();
                        // }
                    }
                    if (Response.toString().contains("application/json")) {
                        if (inputMethod.equals("GET")) {
                            File output = new File("test.json");
                            FileWriter writer = new FileWriter(output);
                            writer.write(Response.toString());
                            writer.flush();
                            writer.close();
                        }
                    }
                    if (Response.toString().contains("image/jpeg")) {
                        if (inputMethod.equals("GET")) {
                            File initialImage = new File("Shot 0006.png");
                            BufferedImage bImage = ImageIO.read(initialImage);
                            //ImageIO.write(bImage, "gif", new File("C://Users/Rou/Desktop/image.gif"));
                            ImageIO.write(bImage, "jpg", new File("TEST.jpg"));
                        }
                    }
                    if (Response.toString().contains(error403)) {
                        if (inputMethod.equals("GET")) {
                            System.out.println("  access to the requested resource is forbidden for some reason.\n" +
                                    "  The server understood the request,\n" +
                                    "  but will not fulfill it due to client-related issues.");
                        }
                    }
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Please enter a COMMAND or URL! This is NOT.");
            }
        }
    }

    private static String makeRequestBody(String resource, String method, String studentId) {
        StringBuilder sb = new StringBuilder();
        sb.append(method).append(" ").append(resource).append(" ").append("HTTP/1.0").append("\r\n");
        sb.append("Accept: */*").append("\r\n");
        sb.append("ACCEPT-ENCODING: gzip, default, br").append("\r\n");
        if (!studentId.isEmpty())
            sb.append("x-student-id: ").append(studentId).append("\r\n");
        sb.append("\r\n");
        sb.append("\r\n");
        return sb.toString();
    }
}
