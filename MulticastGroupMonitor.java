import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

public class MulticastGroupMonitor extends BaseMulticastGroup {

    public int memberCount = 0;
    public int leaveMemberCount = 0;

    public MulticastGroupMonitor(String group) {
        groupName = group;
        groupAddress = getGroupAddress(groupName);
    }

    public static void main(String[] args) {
        String group = args[0];
        String errorMessage = validateArgs(group);
        if (errorMessage.length() > 0) {
            System.out.println(errorMessage);
            return;
        }

        try {
            MulticastSocket ms = new MulticastSocket(MulticastGroupMonitor.PORT);
            MulticastGroupMonitor myGroup = new MulticastGroupMonitor(group);
            myGroup.joinGroup(ms);

            while (true) {
                String message = myGroup.receiveMessage(ms);
                myGroup.processMessage(message);
                if (!message.contains("[FROM:")) {
                    System.out.println("=============== " + myGroup.groupName + " Monitor ===============");
                    System.out.println("Left Group Members: " + myGroup.leaveMemberCount);
                    System.out.println("Total Group Members: " + myGroup.memberCount);
                    System.out.println("=============== END ===============");
                }
            }

        } catch (IOException e) {
            System.out.println("An error has occured");
            return;
        }
    }

    public void joinGroup(MulticastSocket ms) {
        try {
            ms.joinGroup(InetAddress.getByName(groupAddress));

        } catch (IOException e) {
            System.out.println(e.toString());
            System.exit(1);
        }
    }

    public String receiveMessage(MulticastSocket ms) {
        try {
            byte[] buf = new byte[1024];
            DatagramPacket dp = new DatagramPacket(buf, 1024);
            ms.receive(dp);

            String message = new String(dp.getData(), 0, dp.getLength());

            return message;

        } catch (IOException e) {
            return e.toString();
        }
    }

    public void changeMemberCount(Boolean isPositive) {
        if (isPositive) {
            memberCount += 1;
        } else if (memberCount > 0 && !isPositive) {
            memberCount -= 1;
            leaveMemberCount += 1;
        }
    }

    public static String validateArgs(String groupName) {
        String errorMessage = "";

        Boolean groupNameIsValid = Arrays.asList(GROUP_NAMES).contains(groupName);
        if (!groupNameIsValid) {
            errorMessage = "Please select a valid group name from the list below.\n" +
                    "[1] timetable [2] exams [3] classes";
        }

        return errorMessage;
    }

    public void processMessage(String message) {
        String joinString = "[+]";
        String leaveString = "[-]";
        String messageString = "[FROM:";

        if (message.substring(0, 3).equals(joinString)) {
            changeMemberCount(true);
        } else if (message.substring(0, 3).equals(leaveString)) {
            changeMemberCount(false);
        } else if (!(message.substring(0, 6).equals(messageString))) {
            System.out.println("ERROR: " + message);
        }
    }
}
