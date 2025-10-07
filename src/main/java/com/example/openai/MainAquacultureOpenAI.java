package com.example.openai;

import com.example.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstração do sistema de monitoramento aquicultura com OpenAI.
 * 
 * @author Renato V. Mendes
 */
public class MainAquacultureOpenAI {
    
    private static final Logger logger = LoggerFactory.getLogger(MainAquacultureOpenAI.class);
    
    public static void main(String[] args) {
        logger.info("=== SISTEMA DE MONITORAMENTO AQUICULTURA (OpenAI) ===");
        
        // Verificar configurações
        AppConfig config = AppConfig.getInstance();
        config.logConfigInfo();
        
        if (!config.isOpenaiValid()) {
            logger.error("Configurações OpenAI inválidas! Verifique OPENAI_API_KEY no arquivo .env");
            System.exit(1);
        }
        
        try {
            // Criar instância do sistema de monitoramento
            AquacultureOpenAIExample monitoring = new AquacultureOpenAIExample();
            
            // Executar demonstração completa
            monitoring.runMonitoringDemo();
            
        } catch (Exception e) {
            logger.error("Erro durante execução: {}", e.getMessage(), e);
            System.exit(1);
        }
        
        logger.info("=== DEMONSTRAÇÃO FINALIZADA ===");
    }
}