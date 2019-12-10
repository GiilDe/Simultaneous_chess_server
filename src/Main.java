import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;


public class Main {
    private final static int port = 59090;

    private static void decideSides(Socket socket1, Socket socket2) throws IOException {
        Random r = new Random();
        int firstSide = r.nextInt(1);
        int secondSide = 1 - firstSide;

        var outputStream1 = socket1.getOutputStream();
        ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(outputStream1);
        objectOutputStream1.writeObject(firstSide);

        var outputStream2 = socket2.getOutputStream();
        ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(outputStream2);
        objectOutputStream2.writeObject(secondSide);
    }

    private static Move waitForMove(Socket socket) throws IOException, ClassNotFoundException {
        var inputStream = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        int[] move = (int[]) objectInputStream.readObject();
        return new Move(move);
    }

    private static void sendMove(Socket socket, Move move) throws IOException {
        var outputStream = socket.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(move.toIntArray());
    }

    private static boolean isVictory(Move move){
        return move.toIntArray()[0] == -1;
    }

    public static void main(String[] args) {
        runGame();
    }

    private static void runGame()  {
        try (var listener = new ServerSocket(port)) {
            System.out.println("The server is running...");
            Move move1;
            Move move2;
            var socket1 = listener.accept();
            System.out.println("One client connected");
            var socket2 = listener.accept();
            System.out.println("Second client connected");
            decideSides(socket1, socket2);
            while (true) {
                move1 = waitForMove(socket1);
                if(isVictory(move1)){
                    break;
                }
                sendMove(socket2, move1);
                move2 = waitForMove(socket2);
                if(isVictory(move2)){
                    break;
                }
                sendMove(socket1, move2);
            }
            socket1.close();
            socket2.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
}