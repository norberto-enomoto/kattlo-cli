package com.github.kattlo;

import io.quarkus.test.junit.QuarkusTest;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author fabiojose
 */
@QuarkusTest
public class EntryCommandTest {

    EntryCommand entry = new EntryCommand();

    @Test
    public void should_exit_code_2_when_config_file_does_not_exists(){

        String[] args = {
            "--config-file=./src/test/resources/_not_found.yaml",
            "--kafka-cfg=./src/test/resources/kafka.properties",
            "topic",
            "--directory=."
        };

        int actual =
            new CommandLine(entry).execute(args);

        assertEquals(2, actual);
    }

    @Test
    public void should_exit_code_2_when_kafka_cfg_files_does_not_exists() {

        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-cfg=./src/test/resources/__not_found_kafka.properties",
            "topic",
            "--directory=."
        };

        int actual =
            new CommandLine(entry).execute(args);

        assertEquals(2, actual);

    }
}
