package br.gov.saude.sp.ipgg;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.io.FileUtils;


import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

@DisplayName("### Suite de Testes: Extracao de dados do html")
class AppTest {

    File htmlFile = null;

   
    @ParameterizedTest
    @MethodSource("metodoProvedorDeCasosDeTestesParaClassesDeEquivalenciasValidas")
    @DisplayName("Testa resultados finais do algoritmo")    
    void resultCsvFileTest(String htmlInputFileName, String csvOutputFileName, String oracleFile) throws IOException {

        String basedir = System.getProperty("basedir", ".");
        String dirWithfilesForTests = basedir+"/src/test/resources/fileForTests";
        String dirWithOracleFiles = basedir+"/src/test/resources/oracleFiles";
        
        File htmlInputFile = new File(dirWithfilesForTests+"/"+htmlInputFileName);

        String htmlFilePath = dirWithfilesForTests+"/"+htmlInputFileName;
        String resultCsvFilePath = dirWithfilesForTests + "/" + csvOutputFileName;
        String oracleCsvFilePath = dirWithOracleFiles + "/" + oracleFile;

    	App.transformToCsv(htmlInputFile, dirWithfilesForTests+"/"+csvOutputFileName);
        
        File resultCsvFile = FileUtils.getFile(dirWithfilesForTests + "/" + csvOutputFileName);          
        File oracleCsvFile = FileUtils.getFile(dirWithOracleFiles + "/" + oracleFile);
    	assertTrue(FileUtils.contentEquals(resultCsvFile, oracleCsvFile));
    }
    
    private static Collection<Object[]> metodoProvedorDeCasosDeTestesParaClassesDeEquivalenciasValidas() {    	
    	return Arrays.asList(new String[][]{                
        	{"document-AnalitAlfabeGeriatria.pdf.xhmtls.html"  , "dados.csv"  ,"dados.csv"}, 
        	{"document-AnalitAlfabeGeriatria-1.pdf.xhmtls.html", "dados1.csv" ,"dados1.csv"},
                {"document-AnalitAlfabeGeriatria-2.pdf.xhmtls.html", "dados2.csv" ,"dados2.csv"},
                {"document-AnalitAlfabeGeriatria-3.pdf.xhmtls.html", "dados3.csv" ,"dados3.csv"},
                {"document-AnalitAlfabeGeriatria-4.pdf.xhmtls.html", "dados4.csv" ,"dados4.csv"},
                {"document-AnalitAlfabeGeriatria-5.pdf.xhmtls.html", "dados5.csv" ,"dados5.csv"}
    	});   	
    }
}

