package org.example;

import org.example.md.FileTranslator;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.example.server.HttpServer;
import org.example.server.Response;

import java.io.InputStream;

public class Main {
    public static void main(String[] args) {

        Yaml yaml = new Yaml(new Constructor(Config.class));
        InputStream inputStream = Config.class.getClassLoader()
                .getResourceAsStream("config.yaml");
        Config config = yaml.load(inputStream);

        FileTranslator translator = new FileTranslator(config);
        translator.translate();

        Response.setWebRoot(config.getWeb());

        HttpServer server = new HttpServer(translator);
        server.start(config.getPort());
    }
}
