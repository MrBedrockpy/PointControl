package ru.mrbedrockpy.pointcontrol.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.*;
import ru.mrbedrockpy.pointcontrol.PointControl;

import java.io.*;

@NoArgsConstructor
@AllArgsConstructor
public class Config {

    private static final File file = new File("config", PointControl.MOD_ID + ".json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static Config INSTANCE;

    public boolean enableDeassimilationWhenNoPlayers = true;
    public int deassimilationPeriod = 6;

    static  {
        INSTANCE = new Config();
    }

    public static Config getInstance() {
        return INSTANCE != null ? INSTANCE : new Config();
    }

    public static void load() {
        try (FileReader reader = new FileReader(file)) {
            INSTANCE = gson.fromJson(reader, Config.class);
        } catch (FileNotFoundException ignored) {}
        catch (RuntimeException | IOException e) {throw new RuntimeException(e);}
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(INSTANCE, writer);
        } catch (IOException e) {throw new RuntimeException(e);}
    }
}
