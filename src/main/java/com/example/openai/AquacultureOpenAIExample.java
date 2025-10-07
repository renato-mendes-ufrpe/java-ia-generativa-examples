package com.example.openai;

import com.example.config.AppConfig;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Sistema de monitoramento aquicultura com análise via OpenAI.
 * 
 * @author Renato V. Mendes
 */
public class AquacultureOpenAIExample {
    private static final Logger logger = LoggerFactory.getLogger(AquacultureOpenAIExample.class);
    private final AppConfig config;
    private final OpenAIClient client;
    
    public AquacultureOpenAIExample() {
        this.config = AppConfig.getInstance();
        
        // Cria cliente OpenAI oficial com configurações
        this.client = OpenAIOkHttpClient.builder()
            .apiKey(config.getOpenaiApiKey())
            .build();
    }
    
    /**
     * Gera dados simulados de sensores com diferentes cenários
     */
    public SensorData generateSimulatedData(String pondId, String scenario) {
        Random random = new Random();
        
        return switch (scenario) {
            case "normal" -> new SensorData(
                6.5 + random.nextDouble() * 2.0,  // O2: 6.5-8.5 mg/L
                24.0 + random.nextDouble() * 4.0, // Temp: 24-28°C
                7.0 + random.nextDouble() * 1.0,  // pH: 7.0-8.0
                0.1 + random.nextDouble() * 0.4,  // NH3: 0.1-0.5 mg/L
                "Ensolarado, ventos leves",
                pondId
            );
            case "critico" -> new SensorData(
                2.0 + random.nextDouble() * 2.0,  // O2 baixo: 2.0-4.0 mg/L
                30.0 + random.nextDouble() * 3.0, // Temp alta: 30-33°C
                8.5 + random.nextDouble() * 1.0,  // pH alto: 8.5-9.5
                1.5 + random.nextDouble() * 1.0,  // NH3 alto: 1.5-2.5 mg/L
                "Tempestade prevista, alta temperatura",
                pondId
            );
            case "alerta" -> new SensorData(
                5.0 + random.nextDouble() * 1.0,  // O2 médio: 5.0-6.0 mg/L
                28.0 + random.nextDouble() * 2.0, // Temp média: 28-30°C
                6.5 + random.nextDouble() * 0.5,  // pH baixo: 6.5-7.0
                0.8 + random.nextDouble() * 0.4,  // NH3 médio: 0.8-1.2 mg/L
                "Nublado, chuva leve prevista",
                pondId
            );
            default -> generateSimulatedData(pondId, "normal");
        };
    }
    
    /**
     * Analisa os dados com OpenAI e retorna a resposta direta da IA
     */
    public String analyzeWithOpenAI(SensorData sensorData) {
        try {
            String prompt = buildAnalysisPrompt(sensorData);
            
            logger.info("Analisando: {}", sensorData);
            logger.info("--- INÍCIO DO PROMPT ---");
            logger.info(prompt);
            logger.info("--- FIM DO PROMPT ---");
            
            // Configura a requisição usando a nova API oficial
            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addSystemMessage("Você é um especialista em aquicultura com vasta experiência em monitoramento de viveiros de tilápia.")
                .addUserMessage(prompt)
                .model(ChatModel.of(config.getOpenaiModel()))
                .maxCompletionTokens(config.getOpenaiMaxTokens())
                .temperature(config.getOpenaiTemperature())
                .build();
            
            // Faz a chamada para o OpenAI
            ChatCompletion completion = client.chat().completions().create(params);
            
            String response = completion.choices()
                    .get(0)
                    .message()
                    .content()
                    .orElse("Resposta vazia do OpenAI");
            
            logger.info("✅ Análise OpenAI concluída ({} chars)", response.length());
            return response;
            
        } catch (Exception e) {
            logger.error("Erro na análise OpenAI: {}", e.getMessage());
            return "ERRO: Não foi possível obter análise da IA OpenAI. Verifique os sensores manualmente.";
        }
    }
    
    /**
     * Constrói o prompt especializado para análise de aquicultura
     */
    private String buildAnalysisPrompt(SensorData data) {
        return String.format("""
            Analise os dados de sensores em tempo real do viveiro de aquicultura.
            
            DADOS DO VIVEIRO %s:
            • Oxigênio Dissolvido: %.2f mg/L
            • Temperatura da Água: %.1f°C  
            • pH: %.1f
            • Amônia (NH3): %.2f mg/L
            • Previsão Climática: %s
            
            PARÂMETROS IDEAIS PARA TILÁPIA:
            • Oxigênio: 6.0-8.0 mg/L (crítico abaixo de 4.0)
            • Temperatura: 24-28°C (estresse acima de 30°C)
            • pH: 7.0-8.0 (mortalidade fora de 6.5-9.0)
            • Amônia: <0.5 mg/L (tóxico acima de 1.0 mg/L)
            
            FORNEÇA UMA ANÁLISE ESTRUTURADA:
            
            1. NÍVEL DE RISCO: [NORMAL/ALERTA/CRÍTICO]
            
            2. AÇÕES CRÍTICAS (se necessário):
            - Lista de ações URGENTES que devem ser tomadas IMEDIATAMENTE
            - Foque em salvar os peixes e evitar mortalidade
            
            3. AÇÕES PREVENTIVAS:
            - Medidas para otimizar as condições
            - Ajustes de manejo para próximas horas/dias
            
            4. NOTIFICAÇÕES:
            - Quem deve ser alertado
            - Quando reavaliar a situação
            
            Seja ESPECÍFICO e PRÁTICO. Considere que é um sistema automatizado real.
            """, 
            data.getPondId(), data.getOxygenLevel(), data.getWaterTemperature(), 
            data.getPh(), data.getAmmoniaLevel(), data.getWeatherForecast());
    }
    
    /**
     * Demonstração simples do sistema
     */
    public void runMonitoringDemo() {
        logger.info("=== SISTEMA DE MONITORAMENTO AQUICULTURA (OpenAI) ===");
        
        String[] scenarios = {"normal", "alerta", "critico"};
        String[] pondIds = {"TANK-001", "TANK-002", "TANK-003"};
        
        for (int i = 0; i < scenarios.length; i++) {
            logger.info("\n--- CENÁRIO {}: {} ---", i + 1, scenarios[i].toUpperCase());
            
            // Gera dados simulados
            SensorData sensorData = generateSimulatedData(pondIds[i], scenarios[i]);
            
            // Analisa com OpenAI e exibe resposta direta
            String openaiAnalysis = analyzeWithOpenAI(sensorData);
            
            logger.info("\nRESPOSTA DO OPENAI:\n{}", openaiAnalysis);
            
            // Simula intervalo entre análises
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        logger.info("\n=== FIM DA DEMONSTRAÇÃO ===");
    }
}