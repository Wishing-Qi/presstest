package luohuayu.EndMinecraftPlus;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class Config {
    public static Config instance;
    public static void loadConfig() {
        try {
            instance = new Gson().fromJson(new InputStreamReader(new FileInputStream(new File("config.json"))), Config.class);
        } catch (Exception e) {
            Utils.log("配置文件加载失败: " + e.getMessage());
            instance = new Config();
        }
        if (instance.username == null || instance.username.length == 0) instance.username = new String[] {"$rnd"};
        if (instance.register == null || instance.register.length() == 0) instance.register = "/register $pwd $pwd";
        if (instance.messages == null || instance.messages.length == 0) instance.messages = new String[] {"$rnd喵喵喵喵喵~", "$rnd滑稽喵滑稽喵~"};
        if (instance.md5s == null) instance.md5s = new String[] {};
        if (instance.vexview == null) instance.vexview = "1.9";
    }

    public String[] username;
    public String register;
    public String[] messages;
    public String[] md5s;
    public String vexview;

    public String getRegisterCommand(String password) {
        return register.replace("$pwd", password);
    }

    public String getRandMessage() {
        return messages[new Random().nextInt(messages.length)].replace("$rnd", Utils.getRandomString(1, 4));
    }

    public String getRandUsername() {
        String rawName = username[new Random().nextInt(username.length)].replace("$rnd", Utils.getRandomString(4, 12));
        if (rawName.length() >= 15) rawName = rawName.substring(0, 15);
        return rawName;
    }

    public String[] getAnticheatMD5s() {
        return md5s;
    }

    public String getVexviewVersion() {
        return vexview;
    }
}
