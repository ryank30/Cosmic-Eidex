package server;

import account.Account;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for storing and retrieving a list of {@link Account} objects
 * to and from a JSON file located in the user's operating system-specific
 *  application data directory.
 */
public class AccountStorage {
    /**
     * The path to the JSON file used to store account data, resolved based on the user's OS.
     */
    public static final Path DATA_PATH;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Type ACCOUNT_LIST_TYPE = new TypeToken<List<Account>>() {}.getType();

    // Static block initializes the DATA_PATH depending on OS
    static {
        String appData;
        String os = System.getProperty("os.name").toLowerCase();
        if(os.contains("win")) {
            appData = System.getenv("APPDATA");
            if (appData == null) {
                throw new RuntimeException("APPDATA environment variable not set. Are you really on Windows?");
            }
        } else if (os.contains("mac")){
            appData = System.getProperty("user.home") + "/Library/Application Support";
        } else {
            appData = System.getProperty("user.home") +"/.config";
        }
            DATA_PATH = Paths.get(appData, "MyApp", "accounts.json");

    }

    /**
     * Saves a list of Account objects to the JSON file.
     * @param accounts the list of accounts to save
     * @throws IOException if an I/O error occurs while writing the file.
     */
    public static void saveAccounts(List<Account> accounts) throws IOException {
        Files.createDirectories(DATA_PATH.getParent());
        try (Writer writer = new FileWriter(DATA_PATH.toFile())) {
            gson.toJson(accounts, ACCOUNT_LIST_TYPE, writer);
        }
    }

    /**
     * Loads a list of objects from the Jsonfile
     * @return the list of loaded accounts.
     * @throws IOException if an I/O erroe occurs while reading the file.
     */
    public static List<Account> loadAccounts() throws IOException {
        if (!Files.exists(DATA_PATH)) {
            return new ArrayList<>();
        }
        try (Reader reader = new FileReader(DATA_PATH.toFile())) {
            return gson.fromJson(reader, ACCOUNT_LIST_TYPE);
        }
    }
}