package com.apex.springGenerator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.File;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws Exception {

        String jsonFile = "src/resource/Req.json";
//      String jsonFile = "src/json/Res.json";

        String basePackage = "com.apex.springGenerator";
        
        String classnameDTO = "EnvelopeDTO";
        
        String classNameDTONested ="absher";
        
        String classnameEntity = "EnvelopeEntity";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(new File(jsonFile));

        JsonToDtoGenerator dtoGen = new JsonToDtoGenerator();
        dtoGen.generate(classnameDTO, rootNode, basePackage, Paths.get("src"));
        System.out.println(" DTO file Generated --> ");
        
        JsonToDtoGeneratorNested dtoNestGen = new JsonToDtoGeneratorNested();
        dtoNestGen.generate(classNameDTONested, rootNode, basePackage, Paths.get("src"));
        System.out.println(" DTO Nested file Generated --> ");

        JsonToEntityGenerator entityGen = new JsonToEntityGenerator();
        entityGen.generate(classnameEntity, rootNode, basePackage, Paths.get("src"));
        System.out.println(" Entity file Generated --> ");

    }
}

