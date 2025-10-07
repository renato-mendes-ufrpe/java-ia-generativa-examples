package com.example.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe de configuração centralizada que gerencia as variáveis de ambiente e configurações
 * para múltiplos provedores de IA Generativa (Google Gemini e OpenAI).
 * 
 * <p>Esta classe implementa o padrão Singleton para garantir uma única instância de configuração
 * durante toda a execução da aplicação. Carrega automaticamente as configurações do arquivo .env
 * e disponibiliza validação para cada provedor.</p>
 * 
 * <p><b>Provedores suportados:</b></p>
 * <ul>
 *   <li>Google Gemini - Para geração de texto e análise de arquivos</li>
 *   <li>OpenAI - Para geração de texto via ChatCompletion</li>
 * </ul>
 * 
 * <p><b>Exemplo de uso:</b></p>
 * <pre>{@code
 * AppConfig config = AppConfig.getInstance();
 * if (config.isGeminiValid()) {
 *     String apiKey = config.getGoogleApiKey();
 *     // usar Gemini API
 * }
 * }</pre>
 * 
 * @author Renato V. Mendes
 * @version 1.0
 * @since 1.0
 */
public class AppConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    private static AppConfig instance;
    
    private final Dotenv dotenv;
    
    // Configurações Google Gemini
    private final String googleApiKey;
    private final String geminiModel;
    
    // Configurações OpenAI
    private final String openaiApiKey;
    private final String openaiModel;
    private final String openaiBaseUrl;
    private final int openaiMaxTokens;
    private final double openaiTemperature;
    
    // Configurações gerais
    private final String logLevel;
    
    private AppConfig() {
        logger.info("Carregando configurações do arquivo .env");
        
        // Carrega o arquivo .env
        this.dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
        
        // === CONFIGURAÇÕES GOOGLE GEMINI ===
        this.googleApiKey = dotenv.get("GOOGLE_API_KEY");
        this.geminiModel = dotenv.get("GEMINI_MODEL", "gemini-2.5-flash");
        
        // === CONFIGURAÇÕES OPENAI ===
        this.openaiApiKey = dotenv.get("OPENAI_API_KEY");
        this.openaiModel = dotenv.get("OPENAI_MODEL", "gpt-4o-mini");
        this.openaiBaseUrl = dotenv.get("OPENAI_BASE_URL", "https://api.openai.com/v1");
        this.openaiMaxTokens = Integer.parseInt(dotenv.get("OPENAI_MAX_TOKENS", "4096"));
        this.openaiTemperature = Double.parseDouble(dotenv.get("OPENAI_TEMPERATURE", "0.7"));
        
        // === CONFIGURAÇÕES GERAIS ===
        this.logLevel = dotenv.get("LOG_LEVEL", "INFO");
        
        // Configura propriedades do sistema para compatibilidade
        if (googleApiKey != null && !googleApiKey.trim().isEmpty()) {
            System.setProperty("GOOGLE_API_KEY", googleApiKey);
            logger.info("Chave Google API configurada");
        } else {
            logger.warn("GOOGLE_API_KEY não encontrada no .env!");
        }
        
        if (openaiApiKey != null && !openaiApiKey.trim().isEmpty()) {
            logger.info("Chave OpenAI API configurada");
        } else {
            logger.warn("OPENAI_API_KEY não encontrada no .env!");
        }
        
        logger.info("Configurações carregadas - Gemini: {}, OpenAI: {}, Log: {}", 
                geminiModel, openaiModel, logLevel);
    }
    
    /**
     * Obtém a instância singleton da configuração.
     * 
     * <p>Implementa lazy initialization para criar a instância apenas quando necessário.
     * Thread-safe através de inicialização durante o class loading.</p>
     * 
     * @return a instância única de AppConfig
     */
    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }
    
    // === GETTERS GOOGLE GEMINI ===
    
    /**
     * Obtém a chave da API do Google Gemini.
     * 
     * @return a chave da API configurada no arquivo .env ou null se não configurada
     */
    public String getGoogleApiKey() {
        return googleApiKey;
    }
    
    /**
     * Obtém o modelo do Gemini a ser usado nas requisições.
     * 
     * @return o nome do modelo Gemini (padrão: "gemini-2.5-flash")
     */
    public String getGeminiModel() {
        return geminiModel;
    }
    
    // === GETTERS OPENAI ===
    
    /**
     * Obtém a chave da API OpenAI.
     * 
     * @return a chave da API configurada no arquivo .env ou null se não configurada
     */
    public String getOpenaiApiKey() {
        return openaiApiKey;
    }
    
    /**
     * Obtém o modelo OpenAI a ser usado nas requisições.
     * 
     * @return o nome do modelo OpenAI (padrão: "gpt-4o-mini")
     */
    public String getOpenaiModel() {
        return openaiModel;
    }
    
    /**
     * Obtém a URL base da API OpenAI.
     * 
     * @return a URL base configurada (padrão: "https://api.openai.com/v1")
     */
    public String getOpenaiBaseUrl() {
        return openaiBaseUrl;
    }
    
    /**
     * Obtém o número máximo de tokens para requisições OpenAI.
     * 
     * @return o limite máximo de tokens (padrão: 4096)
     */
    public int getOpenaiMaxTokens() {
        return openaiMaxTokens;
    }
    
    /**
     * Obtém o valor da temperatura para controle de criatividade nas respostas OpenAI.
     * 
     * @return valor entre 0.0 (mais determinístico) e 1.0 (mais criativo) - padrão: 0.7
     */
    public double getOpenaiTemperature() {
        return openaiTemperature;
    }
    
    // === GETTERS GERAIS ===
    
    /**
     * Obtém o nível de log configurado.
     * @return nível de log (padrão: "INFO")
     */
    public String getLogLevel() {
        return logLevel;
    }
    
    /**
     * Verifica se as configurações Gemini são válidas.
     * @return true se a API key do Gemini está configurada
     */
    public boolean isGeminiValid() {
        return googleApiKey != null && !googleApiKey.trim().isEmpty();
    }
    
    /**
     * Verifica se as configurações OpenAI são válidas.
     * @return true se a API key do OpenAI está configurada
     */
    public boolean isOpenaiValid() {
        return openaiApiKey != null && !openaiApiKey.trim().isEmpty();
    }
    
    /**
     * Verifica se pelo menos um provedor está configurado.
     * @return true se Gemini ou OpenAI estão válidos
     */
    public boolean isValid() {
        return isGeminiValid() || isOpenaiValid();
    }
    
    /**
     * Exibe informações de configuração no log para debug.
     */
    public void logConfigInfo() {
        logger.info("=== CONFIGURAÇÕES CARREGADAS ===");
        logger.info("Google API Key: {}", googleApiKey != null && !googleApiKey.trim().isEmpty() ? "***configurada***" : "NÃO ENCONTRADA");
        logger.info("Gemini Model: {}", geminiModel);
        logger.info("OpenAI API Key: {}", openaiApiKey != null && !openaiApiKey.trim().isEmpty() ? "***configurada***" : "NÃO ENCONTRADA");
        logger.info("OpenAI Model: {}", openaiModel);
        logger.info("OpenAI Max Tokens: {}", openaiMaxTokens);
        logger.info("OpenAI Temperature: {}", openaiTemperature);
        logger.info("Log Level: {}", logLevel);
        logger.info("================================");
    }
}