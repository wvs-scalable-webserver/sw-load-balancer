package de.wvs.sw.loadbalancer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by Marvin Erkes on 04.02.2020.
 */
public final class FileUtil {

    private static final String STATS_FILE = ".stats";

    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private FileUtil() {
        // No instance
    }

    public static long[] loadStats() {

        long[] result = new long[2];

        // Don't proceed if the file doesn't exists yet
        File file = new File(STATS_FILE);
        if (!file.exists()) {
            return result;
        }

        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            // Total read bytes
            result[0] = Long.valueOf(in.readLine());
            // Total written bytes
            result[1] = Long.valueOf(in.readLine());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return result;
    }

    public static void saveStats(long totalReadBytes, long totalWrittenBytes) {

        try (BufferedWriter out = new BufferedWriter(new FileWriter(STATS_FILE))) {
            out.write(Long.toString(totalReadBytes));
            out.write(System.lineSeparator());
            out.write(Long.toString(totalWrittenBytes));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
