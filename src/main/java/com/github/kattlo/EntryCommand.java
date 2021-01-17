package com.github.kattlo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import com.github.kattlo.topic.TopicCommand;
import com.github.kattlo.util.VersionUtil;

import org.apache.kafka.clients.admin.AdminClientConfig;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

/**
 * @author fabiojose
 */
@TopCommand
@Command(
    name = "kattlo",
    versionProvider = VersionUtil.QuarkusVersionProvider.class,
    mixinStandardHelpOptions = true,
    subcommands = {
        TopicCommand.class,
        InfoCommand.class,
        InitCommand.class
    }
)
@Slf4j
public class EntryCommand {

    private File configuration;
    private Properties configurationValues;

    private File kafkaConfiguration;
    private Properties kafkaConfigurationValues;

    private String bootstrapServers;

    @Spec
    private CommandSpec spec;

    @Option(
        names = {
            "--config-file"
        },
        description = "Kattlo configurations",
        required = true,
        defaultValue = ".kattlo.yaml"
    )
    public void setConfiguration(File configuration) {
        this.configuration = Objects.requireNonNull(configuration);
    }

    public Properties getConfiguration() {
        if(null== configurationValues){
            configurationValues = new Properties();
            try{
                configurationValues.load(new FileInputStream(configuration));
            }catch(IOException e){
                throw new CommandLine
                    .ParameterException(spec.commandLine(),
                        configuration.getAbsolutePath() + " can't be read");
            }
        }

        return configurationValues;
    }

    @Option(
        names = {
            "--kafka-config-file"
        },
        description = "Properties file for Apache Kafka® clients",
        required = true,
        defaultValue = "kafka.properties"
    )
    public void setKafkaConfiguration(File kafkaConfiguration) {
        this.kafkaConfiguration = Objects.requireNonNull(kafkaConfiguration);
    }

    public Properties getKafkaConfiguration() {
        if(null== kafkaConfigurationValues){
            kafkaConfigurationValues = new Properties();

            try{
                kafkaConfigurationValues
                    .load(new FileReader(kafkaConfiguration));

                if(Objects.nonNull(getBootstrapServers())){
                    var oldBootstrapServers =
                      kafkaConfigurationValues
                        .put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                            getBootstrapServers());

                    log.debug("bootstrap.servers overwritten by {}", getBootstrapServers());
                    log.debug("Old bootstrap.servers {}", oldBootstrapServers);
                }
            }catch(IOException e){
                throw new CommandLine
                    .ParameterException(spec.commandLine(),
                        kafkaConfiguration.getAbsolutePath() + " can't be read");
            }
        }
        return kafkaConfigurationValues;
    }

    @Option(
        names = {
            "--bootstrap-servers"
        },
        description = "host/port pairs to connect the Apache Kafka®",
        required = false
    )
    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }
    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void validateOptions() {
        if(!configuration.exists()){
            throw new CommandLine.
                ParameterException(spec.commandLine(),
                        configuration.getAbsolutePath() + " not found");
        }

        if(!kafkaConfiguration.exists()){
            throw new CommandLine.
                ParameterException(spec.commandLine(),
                    kafkaConfiguration.getAbsolutePath() + " not found");
        }
    }
}
