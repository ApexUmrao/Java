package com.apex.springGenerator;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.javapoet.*;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;

public class JsonToDtoGenerator {

    public void generate(String className,
                         JsonNode node,
                         String basePackage,
                         Path outputDir) throws IOException {

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(ClassName.get("lombok", "Data"))
                .addAnnotation(ClassName.get("lombok", "Builder"))
                .addAnnotation(ClassName.get("lombok", "NoArgsConstructor"))
                .addAnnotation(ClassName.get("lombok", "AllArgsConstructor"));

        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String fieldName = field.getKey();
            JsonNode value = field.getValue();

            if (value.isObject()) {
                String nestedClassName = NamingUtil.toClassName(fieldName);

                generate(nestedClassName, value, basePackage, outputDir);

                classBuilder.addField(
                        ClassName.get(basePackage + ".dto", nestedClassName),
                        fieldName,
                        Modifier.PRIVATE
                );

            } else if (value.isArray()) {
                JsonNode first = value.get(0);

                if (first != null && first.isObject()) {
                    String nestedClassName = NamingUtil.toClassName(fieldName);

                    generate(nestedClassName, first, basePackage, outputDir);

                    classBuilder.addField(
                            ParameterizedTypeName.get(
                                    ClassName.get("java.util", "List"),
                                    ClassName.get(basePackage + ".dto", nestedClassName)
                            ),
                            fieldName,
                            Modifier.PRIVATE
                    );
                }
            } else {

                FieldSpec fieldSpec = FieldSpec.builder(
                                TypeResolver.resolve(value),
                                fieldName,
                                Modifier.PRIVATE
                        )
                        .addAnnotation(ClassName.get("jakarta.validation.constraints", "NotNull"))
                        .build();

                classBuilder.addField(fieldSpec);
            }
        }

        JavaFile.builder(basePackage + ".dto", classBuilder.build())
                .build()
                .writeTo(outputDir);
    }
}

