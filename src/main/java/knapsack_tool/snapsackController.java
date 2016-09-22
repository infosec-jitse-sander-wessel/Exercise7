package knapsack_tool;

import models.PrivateKey;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by wessel on 9/19/16.
 */
public class snapsackController {
    private final Options options;
    private final CommandLine commandLine;
    private PrivateKey privateKey;
    private String publicKey;

    private snapsackController(String[] args) throws Exception {
        this.options = getOptions();
        CommandLineParser parser = new BasicParser();

        try {
            CommandLine commandLine = parser.parse(options, args);

            if (commandLine.getArgs().length != 1) {
                throw new ParseException("A key file is required.");
            }

            this.commandLine = commandLine;
            if (commandLine.hasOption("d")) {
                privateKey = PrivateKey.parseFromFile(commandLine.getArgs()[0]);
            } else {
                publicKey = commandLine.getArgs()[0];
            }
        } catch (ParseException e) {
            System.out.println("Incorrect arguments:" + e.getMessage());
            printHelpPage();
            throw new Exception("incorrect input program should close");
        }
    }

    public static void main(String[] args) {
        try {
            snapsackController snapsackController = new snapsackController(args);
            snapsackController.run();
        } catch (Exception e) {
            System.out.println("Application quit due to: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("d", "decrypt", false, "decrypt");
        options.addOption("h", "help", false, "Show this help page");
        options.addOption("f", "file", true, "read from given file");
        return options;
    }

    private void printHelpPage() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("[-h] [-d] <privateKey file>",
                "En/Decrypts stdin to stdout. using the super increasing knapsack public encryption",
                options, "");
    }

    private void run() throws Exception {
        if (commandLine.hasOption("h")) {
            printHelpPage();
            return;
        }

        System.out.println("Running Snapsack with options -d: " + commandLine.hasOption('d') +
                " , -f: " + commandLine.getOptionValue('f') +
                " and keyFile: " + commandLine.getArgs()[0]);

        Knapsack knapsack = new Knapsack();

        if (commandLine.hasOption("f")) {
            File file = new File(commandLine.getOptionValue("f"));
            System.setIn(new FileInputStream(file));
        }

        if (commandLine.hasOption("d")) {
            knapsack.decrypt(System.in, privateKey);
        } else {
            knapsack.encrypt(System.in, publicKey);
        }
    }
}
