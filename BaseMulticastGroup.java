import java.util.Arrays;

public class BaseMulticastGroup {
    public static final int PORT = 5000;
    public static final String[] GROUP_ADDRESSES = { "226.4.5.6", "226.4.5.7", "226.4.5.8" };
    public static final String[] GROUP_NAMES = { "timetable", "exams", "classes" };

    public String groupName;
    public String groupAddress;

    public String getGroupAddress(String groupName) {
        int groupNameIndex = Arrays.asList(GROUP_NAMES).indexOf(groupName);
        return GROUP_ADDRESSES[groupNameIndex];
    }
}
