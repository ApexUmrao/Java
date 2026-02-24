package com.apex.springGenerator;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.javapoet.*;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;

public class JsonToDtoGeneratorNested {

	public void generate(String className,
            JsonNode node,
            String basePackage,
            Path outputDir) throws IOException {

		TypeSpec typeSpec = buildClass(className, node);

		JavaFile.builder(basePackage + ".dto", typeSpec)
				.build()
				.writeTo(outputDir);
  }
	
	private TypeSpec buildClass(String className, JsonNode node) {

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

	            TypeSpec nestedClass = buildNestedClass(nestedClassName, value);

	            classBuilder.addType(nestedClass);

	            classBuilder.addField(
	                    ClassName.bestGuess(nestedClassName),
	                    fieldName,
	                    Modifier.PRIVATE
	            );

	        } else if (value.isArray()) {

	            JsonNode first = value.get(0);

	            if (first != null && first.isObject()) {

	                String nestedClassName = NamingUtil.toClassName(fieldName);

	                TypeSpec nestedClass = buildNestedClass(nestedClassName, first);

	                classBuilder.addType(nestedClass);

	                classBuilder.addField(
	                        ParameterizedTypeName.get(
	                                ClassName.get("java.util", "List"),
	                                ClassName.bestGuess(nestedClassName)
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

	    return classBuilder.build();
	}
	
	private TypeSpec buildNestedClass(String className, JsonNode node) {

	    TypeSpec.Builder nestedBuilder = TypeSpec.classBuilder(className)
	            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
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

	            TypeSpec innerNested = buildNestedClass(nestedClassName, value);

	            nestedBuilder.addType(innerNested);

	            nestedBuilder.addField(
	                    ClassName.bestGuess(nestedClassName),
	                    fieldName,
	                    Modifier.PRIVATE
	            );

	        } else if (value.isArray()) {

	            JsonNode first = value.get(0);

	            if (first != null && first.isObject()) {

	                String nestedClassName = NamingUtil.toClassName(fieldName);

	                TypeSpec innerNested = buildNestedClass(nestedClassName, first);

	                nestedBuilder.addType(innerNested);

	                nestedBuilder.addField(
	                        ParameterizedTypeName.get(
	                                ClassName.get("java.util", "List"),
	                                ClassName.bestGuess(nestedClassName)
	                        ),
	                        fieldName,
	                        Modifier.PRIVATE
	                );
	            }

	        } else {

	            nestedBuilder.addField(
	                    FieldSpec.builder(
	                                    TypeResolver.resolve(value),
	                                    fieldName,
	                                    Modifier.PRIVATE
	                            )
	                            .addAnnotation(ClassName.get("jakarta.validation.constraints", "NotNull"))
	                            .build()
	            );
	        }
	    }

	    return nestedBuilder.build();
	}
}

