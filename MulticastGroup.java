import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.Scanner;

public class MulticastGroup implements Runnable {
    public static final int PORT = 5000;
    public static final String[] GROUP_ADDRESSES = { "226.4.5.6", "226.4.5.7", "226.4.5.8" };
    public static final String[] GROUP_NAMES = { "timetable", "exams", "classes" };

    public static Scanner scanner = new Scanner(System.in);

    public String groupName;
    public String groupAddress;
    public String username;

    public MulticastGroup(String group, String name) {
        groupName = group;
        username = name;
        groupAddress = setGroupAddress(groupName);
    }

    public static void main(String[] args) {
        String group = args[0];
        String username = args[1];

        String errorMessage = MulticastGroup.validateArgs(group, username);
        if (errorMessage.length() > 0) {
            System.out.println(errorMessage);
            return;
        }

        try {
            MulticastSocket ms = new MulticastSocket(MulticastGroup.PORT);
            MulticastGroup myGroup = new MulticastGroup(group, username);

            myGroup.joinGroup(ms);
            Thread thread = new Thread(myGroup);
            thread.start();

            while (thread.isAlive()) {
                myGroup.receiveMessage(ms);
            }
            scanner.close();

        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    // Group action methods
    public void joinGroup(MulticastSocket ms) {
        try {
            ms.joinGroup(InetAddress.getByName(groupAddress));
            String message = "[+] " + username + " has joined.";
            sendMessage(message, ms);

        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public void leaveGroup(MulticastSocket ms) {
        try {
            String message = "[-] " + username + " has left.";
            sendMessage(message, ms);
            ms.leaveGroup(InetAddress.getByName(groupAddress));

        } catch (IOException e) {
            System.exit(0);
        }

    }

    public void sendMessage(String message, MulticastSocket ms) {
        try {
            DatagramPacket dp = new DatagramPacket(
                    message.getBytes(), message.length(),
                    InetAddress.getByName(groupAddress), PORT);
            ms.send(dp);

        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public void receiveMessage(MulticastSocket ms) {
        try {
            byte[] buf = new byte[1024];
            DatagramPacket dp = new DatagramPacket(buf, 1024);
            ms.receive(dp);

            String message = new String(dp.getData(), 0, dp.getLength());
            System.out.println(message);

        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    // Utilities
    public static String validateArgs(String groupName, String username) {
        String errorMessage = "";

        Boolean groupNameIsValid = Arrays.asList(GROUP_NAMES).contains(groupName);
        if (!groupNameIsValid) {
            errorMessage = "Please select a valid group name from the list below.\n" +
                    "[1] timetable [2] exams [3] classes";
        } else if (username.length() <= 0) {
            errorMessage = "Please enter a valid username.";
        }

        return errorMessage;
    }

    public String setGroupAddress(String groupName) {
        int groupNameIndex = Arrays.asList(GROUP_NAMES).indexOf(groupName);
        return GROUP_ADDRESSES[groupNameIndex];
    }

    public void run() {
        while (true) {
            String message = scanner.nextLine();

            try {
                if (message.length() > 0) {
                    MulticastSocket ms = new MulticastSocket();

                    if (message.equals(".exit")) {
                        leaveGroup(ms);
                        break;
                    } else {
                        message = "[FROM: " + username + "] " + message;
                        sendMessage(message, ms);
                    }
                }

            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
    }
}
