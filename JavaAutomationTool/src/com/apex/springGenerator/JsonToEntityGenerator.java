package com.apex.springGenerator;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;

public class JsonToEntityGenerator {

    public void generate(String className,
                         JsonNode node,
                         String basePackage,
                         Path outputDir) throws IOException {

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(ClassName.get("jakarta.persistence", "Entity"))
                .addAnnotation(ClassName.get("jakarta.persistence", "Table"))
                .addAnnotation(ClassName.get("lombok", "Getter"))
                .addAnnotation(ClassName.get("lombok", "Setter"));

        classBuilder.addField(FieldSpec.builder(Long.class, "id", Modifier.PRIVATE)
                .addAnnotation(ClassName.get("jakarta.persistence", "Id"))
                .addAnnotation(ClassName.get("jakarta.persistence", "GeneratedValue"))
                .build());

        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();

            FieldSpec fieldSpec = FieldSpec.builder(
                    TypeResolver.resolve(field.getValue()),
                    field.getKey(),
                    Modifier.PRIVATE
            ).build();

            classBuilder.addField(fieldSpec);
        }

        JavaFile.builder(basePackage + ".entity", classBuilder.build())
                .build()
                .writeTo(outputDir);
    }
}

