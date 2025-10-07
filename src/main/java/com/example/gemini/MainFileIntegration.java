package com.example.gemini;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstração de integração com arquivos usando Gemini Files API.
 * 
 * @author Renato V. Mendes
 */
public class MainFileIntegration {
    
    private static final Logger logger = LoggerFactory.getLogger(MainFileIntegration.class);
    
    public static void main(String[] args) {
        logger.info("=== DEMONSTRAÇÃO DE INTEGRAÇÃO COM ARQUIVOS - GEMINI ===");
        
        try {
            GeminiFileExample fileExample = new GeminiFileExample();
            
            // === ANÁLISE 1: PROJETO PDF ===
            System.out.println("📄 ANÁLISE 1: Resumo estruturado do projeto (PDF)");
            System.out.println("=".repeat(80));
            
            String projectAnalysis = fileExample.analyzeProjectWithGemini();
            System.out.println(projectAnalysis);
            
            System.out.println("\n" + "=".repeat(80));
            
            // === ANÁLISE 2: ARQUIVO DE LOG ===
            System.out.println("📊 ANÁLISE 2: Arquivo de log da aplicação");
            System.out.println("=".repeat(80));
            
            String logAnalysis = fileExample.analyzeLogFile();
            System.out.println(logAnalysis);
            
        } catch (Exception e) {
            logger.error("Erro durante análise: {}", e.getMessage(), e);
            System.err.println("❌ Erro: " + e.getMessage());
        }
        
        logger.info("=== DEMONSTRAÇÃO CONCLUÍDA ===");
    }
}