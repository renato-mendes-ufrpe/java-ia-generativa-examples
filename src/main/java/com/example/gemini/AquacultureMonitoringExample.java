package com.example.gemini;

import com.example.config.AppConfig;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Random;

/**
 * Sistema de monitoramento aquicultura com análise via Gemini.
 * 
 * @author Renato V. Mendes
 */
public class AquacultureMonitoringExample {
    private static final Logger logger = LoggerFactory.getLogger(AquacultureMonitoringExample.class);
    private final AppConfig config;
    private final String modelName;
    private final Client client;
    
    public AquacultureMonitoringExample() {
        this.config = AppConfig.getInstance();
        this.modelName = config.getGeminiModel();
        
        // Cria cliente Gemini com a chave API
        this.client = Client.builder()
                .apiKey(config.getGoogleApiKey())
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
     * Analisa os dados com Gemini e retorna a resposta direta da IA
     */
    public String analyzeWithGemini(SensorData sensorData) {
        try {
            String prompt = buildAnalysisPrompt(sensorData);
            
            logger.info("Analisando: {}", sensorData);
            logger.info("--- INÍCIO DO PROMPT ---");
            logger.info(prompt);
            logger.info("--- FIM DO PROMPT ---");
            
            // Cria o conteúdo da consulta
            Content content = Content.builder()
                    .parts(Arrays.asList(Part.fromText(prompt)))
                    .build();
            
            // Faz a chamada para o Gemini
            GenerateContentResponse response = client.models.generateContent(
                modelName,
                Arrays.asList(content),
                null
            );
            
            return response.text();
            
        } catch (Exception e) {
            logger.error("Erro na análise: {}", e.getMessage());
            return "ERRO: Não foi possível obter análise da IA. Verifique os sensores manualmente.";
        }
    }
    
    /**
     * Constrói o prompt especializado para análise de aquicultura
     */
    private String buildAnalysisPrompt(SensorData data) {
        return String.format("""
            Você é um especialista em aquicultura analisando dados de sensores em tempo real.
            
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
        logger.info("=== SISTEMA DE MONITORAMENTO AQUICULTURA ===");
        
        String[] scenarios = {"normal", "alerta", "critico"};
        String[] pondIds = {"TANK-001", "TANK-002", "TANK-003"};
        
        for (int i = 0; i < scenarios.length; i++) {
            logger.info("\n--- CENÁRIO {}: {} ---", i + 1, scenarios[i].toUpperCase());
            
            // Gera dados simulados
            SensorData sensorData = generateSimulatedData(pondIds[i], scenarios[i]);
            
            // Analisa com Gemini e exibe resposta direta
            String geminiAnalysis = analyzeWithGemini(sensorData);
            
            logger.info("\nRESPOSTA DO GEMINI:\n{}", geminiAnalysis);
            
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