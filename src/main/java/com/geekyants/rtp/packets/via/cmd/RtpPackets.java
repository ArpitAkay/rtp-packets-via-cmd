package com.geekyants.rtp.packets.via.cmd;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class RtpPackets {

    @PostConstruct
    public void init() {
        System.out.println("RtpPackets.init()");
        try {
            // Define the tshark command
            String[] command = {
                    "tshark",
                    "-i",
                    "enp0s1",
                    "-f",
                    "udp",
                    "-T",
                    "fields",
                    "-e",
                    "rtp.ssrc"
            };

            // Create a ProcessBuilder to run the command
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Read the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            int lineCount = 0;
            String ssrcValue = null;

            while ((line = reader.readLine()) != null) {
                // Check if the line is not empty
                if (!line.isEmpty()) {
                    lineCount++;
                    // If it's the second non-empty line, store the value and break
                    if (lineCount == 2) {
                        ssrcValue = line;
                        break;
                    }
                }
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("The second non-empty rtp.ssrc value is: " + ssrcValue);
            } else {
                System.err.println("Error executing tshark command.");
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
