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
        if (instance.register == null || instance.register.length() == 0) instance.register = "/register $pwd $pwd";
        if (instance.messages == null || instance.messages.length == 0) instance.messages = new String[] {"$rnd喵喵喵喵喵~", "$rnd滑稽喵滑稽喵~"};
    }

    public String register;
    public String[] messages;

    public String getRegisterCommand(String password) {
        return register.replace("$pwd", password);
    }

    public String getRandMessage() {
        return messages[new Random().nextInt(messages.length)].replace("$rnd", Utils.getRandomString(1, 4));
    }
}
