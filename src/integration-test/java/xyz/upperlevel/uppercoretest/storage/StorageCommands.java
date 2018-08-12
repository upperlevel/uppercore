package xyz.upperlevel.uppercoretest.storage;

import org.json.simple.JSONObject;
import xyz.upperlevel.uppercore.command.CommandContext;
import xyz.upperlevel.uppercore.command.NodeCommand;
import xyz.upperlevel.uppercore.command.functional.AsCommand;
import xyz.upperlevel.uppercore.command.functional.FunctionalCommand;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercoretest.UppercoreTest;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.ChatColor.*;

public class StorageCommands extends NodeCommand {
    public StorageCommands() {
        super("storage");
        FunctionalCommand.inject(this, this);
    }

    @SuppressWarnings("unchecked")
    @AsCommand
    public void ping(CommandContext context) {
        context.send("Pinging...");

        // ------------------------------------ Build data
        Map<String, Object> data = new HashMap<>();
        data.put("string", "hello_world");
        data.put("number", 3);

        Map<String, Object> subData = new HashMap<>();
        subData.put("decimal", 10.20f);
        data.put("map", subData);

        context.send(GRAY + "Data ready to be sent.");

        // ------------------------------------ Send data
        context.send(GRAY + "Sending data...");
        long time = System.currentTimeMillis();
        data.put("time", time);
        UppercoreTest
                .database()
                .table("test")
                .element(context.sender().getName())
                .update(data);
        context.send(GRAY + "Data sent. Time took: " + (System.currentTimeMillis() - time) + " ms.");

        // ------------------------------------ Read data
        context.send(GRAY + "Receiving data...");
        time = System.currentTimeMillis();
        Map<String, Object> received = UppercoreTest
                .database()
                .table("test")
                .element(context.sender().getName())
                .getAll();
        context.send(GRAY + "Data received. Time took: " + (System.currentTimeMillis() - time) + " ms.");

        // ------------------------------------ Test data
        Config reader = received::get;
        boolean failed = false;

        String string = reader.getStringRequired("string");
        if (!string.equals("hello_world")) {
            context.send(RED + "String check failed.");
            failed = true;
        }

        int number = reader.getIntRequired("number");
        if (number != 3) {
            context.send(RED + "Number check failed.");
            failed = true;
        }

        Config subReader = reader.getConfigRequired("map");
        float decimal = subReader.getFloat("decimal");
        if (decimal != 10.20f) {
            context.send(RED + "Decimal check failed.");
            failed = true;
        }

        // ------------------------------------ Success or write error
        if (failed) {
            context.send(RED + "Test failed:");
            context.send(RED + new JSONObject(received).toJSONString());
        } else {
            context.send(GREEN + "Test passed! Go checkout your storage.");
        }
    }
}
