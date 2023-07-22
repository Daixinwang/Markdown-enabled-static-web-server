package org.example.md;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class YamlParser {
    private final Yaml yaml = new Yaml(new Constructor(Article.class));

    public Article yaml2Article(String yamlText) {
        return yaml.load(yamlText);
    }
}
