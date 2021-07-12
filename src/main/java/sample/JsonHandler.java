package sample;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

public class JsonHandler {

    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String USER_HOME = System.getProperty("user.home");
    public static String COMP_NAME;
    public String selJobID;
    public String selJobDesc;
    public String name;
    public static String hostName;
    public static String user;
    public static String pass;
    public static JSONObject property;
    public static boolean exists = false;

    //Initializes properties
    static {
        try {
            COMP_NAME = Inet4Address.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        final File file = new File(USER_DIR + "\\properties.json");
        if (file.exists()) {
            exists = true;
        } else {
            property = new JSONObject();
            property.put("User", "User");
            property.put("Pass", "idi8tangos88admin");
            property.put("HostName", "192.168.1.147");
            writeJson();
        }
        property = readJson();
        user = property.get("User").toString();
        pass = property.get("Pass").toString();
        hostName = property.get("HostName").toString();
    }


    public static JSONObject readJson() {

        Object obj = null;
        try {
            obj = new JSONParser().parse(new FileReader(USER_DIR + "\\properties.json"));
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            Main.LOGGER.log(Level.SEVERE, "There was an error reading the properties file!", e);
        }

        return (JSONObject) obj;
    }

    public static void writeJson() {
        try {
            PrintWriter pw = new PrintWriter(USER_DIR + "\\properties.json");
            pw.write(property.toJSONString());
            pw.flush();
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Main.LOGGER.log(Level.WARNING, "There was an error writing to the properties file!", e);

        }
    }

    public static Object getValue(String key) throws FileNotFoundException {
        return property.get(key);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSelJobID() {
        return selJobID;
    }

    public void setSelJobID(String selJobID) {
        this.selJobID = selJobID;
    }

    public String getSelJobDesc() {
        return selJobDesc;
    }

    public void setSelJobDesc(String selJobDesc) {
        this.selJobDesc = selJobDesc;
    }
}
