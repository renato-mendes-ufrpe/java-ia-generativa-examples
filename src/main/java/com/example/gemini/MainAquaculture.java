package com.example.gemini;

import com.example.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstração do sistema de monitoramento aquicultura com Gemini.
 * 
 * @author Renato V. Mendes
 */
public class MainAquaculture {
    
    private static final Logger logger = LoggerFactory.getLogger(MainAquaculture.class);
    
    public static void main(String[] args) {
        logger.info("=== SISTEMA DE MONITORAMENTO AQUICULTURA ===");
        
        // Verificar configurações
        AppConfig config = AppConfig.getInstance();
        config.logConfigInfo();
        
        if (!config.isValid()) {
            logger.error("Configuração inválida! Verifique o arquivo .env");
            System.exit(1);
        }
        
        try {
            // Criar instância do sistema de monitoramento
            AquacultureMonitoringExample monitoring = new AquacultureMonitoringExample();
            
            // Executar demonstração completa
            monitoring.runMonitoringDemo();
            
        } catch (Exception e) {
            logger.error("Erro durante execução: {}", e.getMessage(), e);
            System.exit(1);
        }
        
        logger.info("=== DEMONSTRAÇÃO FINALIZADA ===");
    }
}