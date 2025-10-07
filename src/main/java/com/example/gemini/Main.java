package com.example.gemini;

import com.example.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe principal para demonstrar chamadas básicas ao Gemini API.
 * 
 * @author Renato V. Mendes
 */
public class Main {
    
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        logger.info("=== Iniciando demonstração básica da API Gemini ===");
        
        // Carrega as configurações do arquivo .env
        AppConfig config = AppConfig.getInstance();
        config.logConfigInfo();
        
        // Verifica se as configurações são válidas
        if (!config.isValid()) {
            logger.error("Configuração inválida! Verifique se GOOGLE_API_KEY está definida no arquivo .env");
            System.err.println("ERRO: GOOGLE_API_KEY não encontrada no arquivo .env");
            System.err.println("Configure o arquivo .env com sua chave da API do Google Gemini");
            System.exit(1);
        }
        
        try {
            // Demonstração de chamadas básicas
            demonstrateBasicCalls();
            
        } catch (Exception e) {
            logger.error("Erro durante execução dos exemplos: {}", e.getMessage(), e);
            System.err.println("Erro: " + e.getMessage());
            System.exit(1);
        }
        
        logger.info("=== Demonstração concluída ===");
    }
    
    /**
     * Demonstra as chamadas básicas ao Gemini API.
     */
    private static void demonstrateBasicCalls() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("DEMONSTRAÇÃO DE CHAMADAS BÁSICAS - GEMINI API");
        System.out.println("=".repeat(50));
        
        GeminiBasicExample basicExample = new GeminiBasicExample();
        
        // Exemplo 1: Geração de texto simples
        System.out.println("\n--- Exemplo 1: Pergunta simples ---");
        String response1 = basicExample.generateSimpleText(
            "O que é inteligência artificial?"
        );
        System.out.println("Resposta: " + response1);
        
        // Exemplo 2: Conversa com contexto
        System.out.println("\n--- Exemplo 2: Conversa com contexto ---");
        String response2 = basicExample.generateWithContext(
            "Sou iniciante em programação Java.",
            "Preciso de dicas para aprender melhor.",
            "Quais são os primeiros conceitos que devo focar?"
        );
        System.out.println("Resposta: " + response2);

        // Exemplo 3: Informações detalhadas da resposta
        System.out.println("\n--- Exemplo 3: Resposta com detalhes técnicos ---");
        basicExample.generateWithDetails(
            "Explique a diferença entre ArrayList e LinkedList em Java."
        );
    }

}