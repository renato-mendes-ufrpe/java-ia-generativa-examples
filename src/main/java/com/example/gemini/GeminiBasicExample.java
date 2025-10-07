package com.example.gemini;

import com.example.config.AppConfig;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.GenerateContentConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exemplos básicos de integração com a API do Google Gemini.
 * 
 * @author Renato V. Mendes
 */
public class GeminiBasicExample {
    
    private static final Logger logger = LoggerFactory.getLogger(GeminiBasicExample.class);
    private final AppConfig config;
    private final String modelName;
    private final Client client;
    
    public GeminiBasicExample() {
        // Carrega as configurações do .env
        this.config = AppConfig.getInstance();
        this.modelName = config.getGeminiModel();
        
        // Verifica se a API key está disponível
        String apiKey = config.getGoogleApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("GOOGLE_API_KEY não encontrada no arquivo .env");
        }
        
        logger.info("Configurando API key do .env para o SDK usando Builder");
        
        // Usa o Client.Builder para configurar a API key diretamente
        // Esta é a forma oficial recomendada pelo SDK
        this.client = Client.builder()
            .apiKey(apiKey)
            .build();
        
        logger.info("GeminiBasicExample inicializado com modelo: {}", modelName);
        logger.info("Client configurado com API Key: {}", apiKey.substring(0, Math.min(10, apiKey.length())) + "...");
    }
    
    /**
     * Gera texto simples usando Gemini.
     * @param prompt o texto de entrada
     * @return resposta gerada pela IA
     */
    public String generateSimpleText(String prompt) {
        try {
            logger.info("Enviando prompt: {}", prompt);
            
            GenerateContentResponse response = client.models.generateContent(
                modelName, 
                prompt,
                null
            );
            
            String result = response.text();
            logger.info("Resposta recebida com {} caracteres", result.length());
            
            return result;
        } catch (Exception e) {
            logger.error("Erro ao gerar texto: {}", e.getMessage(), e);
            throw new RuntimeException("Falha na geração de texto", e);
        }
    }
    
    /**
     * Gera texto considerando múltiplas mensagens como contexto.
     * @param messages array de mensagens para contexto
     * @return resposta considerando todo o contexto
     */
    public String generateWithContext(String... messages) {
        try {
            logger.info("=== Iniciando conversa com contexto ===");
            logger.info("Número de mensagens recebidas: {}", messages.length);
            
            // Log de cada mensagem individual
            for (int i = 0; i < messages.length; i++) {
                logger.info("Mensagem {}: '{}'", (i + 1), messages[i]);
            }
            
            // Combina todas as mensagens em um contexto único
            StringBuilder contextBuilder = new StringBuilder();
            for (int i = 0; i < messages.length; i++) {
                contextBuilder.append("Mensagem ").append(i + 1).append(": ").append(messages[i]).append("\n");
            }
            
            String combinedContext = contextBuilder.toString() + "\nResponda considerando todo o contexto acima:";
            
            // Log do contexto final que será enviado à API
            logger.info("Contexto combinado que será enviado à API:");
            logger.info("--- INÍCIO DO CONTEXTO ---");
            logger.info("{}", combinedContext);
            logger.info("--- FIM DO CONTEXTO ---");
            
            GenerateContentResponse response = client.models.generateContent(
                modelName,
                combinedContext,
                null
            );
            
            String result = response.text();
            logger.info("Resposta da conversa recebida com {} caracteres", result.length());
            
            return result;
        } catch (Exception e) {
            logger.error("Erro ao gerar texto com contexto: {}", e.getMessage(), e);
            throw new RuntimeException("Falha na geração de texto com contexto", e);
        }
    }
    
    /**
     * Gera texto com metadados detalhados incluindo múltiplos candidates.
     * @param prompt o texto de entrada
     */
    public void generateWithDetails(String prompt) {
        try {
            logger.info("Gerando texto com detalhes para: {} (com 2 candidates)", prompt);
            
            // Cria configuração para gerar 2 candidates
            GenerateContentConfig config = GenerateContentConfig.builder()
                .candidateCount(2)  // Solicita 2 versões diferentes da resposta
                .temperature(0.7f)  // Um pouco de criatividade (0.0 = determinístico, 1.0 = muito criativo)
                .build();
            
            GenerateContentResponse response = client.models.generateContent(
                modelName,
                prompt,
                config  // Agora usa a configuração com 2 candidates
            );
            
            System.out.println("=== RESPOSTA DETALHADA COM MÚLTIPLOS CANDIDATES ===");
            
            // Exibe todos os candidates
            if (response.candidates().isPresent()) {
                var candidates = response.candidates().get();
                System.out.println("Número de candidatos gerados: " + candidates.size());
                
                for (int i = 0; i < candidates.size(); i++) {
                    System.out.println("\n--- CANDIDATE " + (i + 1) + " ---");
                    
                    // Extrai o texto do candidate de forma segura
                    var candidate = candidates.get(i);
                    if (candidate.content().isPresent() && candidate.content().get().parts().isPresent()) {
                        var parts = candidate.content().get().parts().get();
                        if (!parts.isEmpty()) {
                            System.out.println("Texto: " + parts.get(0).text());
                        } else {
                            System.out.println("Texto não disponível para este candidate (parts vazia)");
                        }
                    } else {
                        System.out.println("Texto não disponível para este candidate (content/parts ausente)");
                    }
                    
                    // Extrai o valor do Optional para a razão de finalização
                    String finishReason = candidate.finishReason()
                        .map(Object::toString)
                        .orElse("N/A");
                    System.out.println("Razão de finalização: " + finishReason);
                }
            } else {
                // Fallback para o texto principal se candidates não estiver disponível
                System.out.println("Texto: " + response.text());
            }
            
            System.out.println("\n=== INFORMAÇÕES DE TOKENS E METADATA ===");
            
            // Informações sobre tokens (se disponíveis) - extrai valores dos Optional
            if (response.usageMetadata().isPresent()) {
                var usage = response.usageMetadata().get();
                
                // Extrai os valores dos Optional para exibição limpa
                int promptTokens = usage.promptTokenCount().orElse(0);
                int candidatesTokens = usage.candidatesTokenCount().orElse(0);
                int totalTokens = usage.totalTokenCount().orElse(0);
                
                System.out.println("Tokens de entrada: " + promptTokens);
                System.out.println("Tokens de saída: " + candidatesTokens);
                System.out.println("Total de tokens: " + totalTokens);
            } else {
                System.out.println("Informações de usage não disponíveis");
            }
            
            // Informações gerais sobre candidates
            if (response.candidates().isPresent()) {
                var candidates = response.candidates().get();
                System.out.println("Número total de candidatos: " + candidates.size());
                
                // Razão de finalização geral (sem duplicação)
                if (!candidates.isEmpty()) {
                    String generalFinishReason = candidates.get(0).finishReason()
                        .map(Object::toString)
                        .orElse("N/A");
                    System.out.println("Status geral da resposta: " + generalFinishReason);
                }
            } else {
                System.out.println("Informações de candidatos não disponíveis");
            }
            
        } catch (Exception e) {
            logger.error("Erro ao gerar texto com detalhes: {}", e.getMessage(), e);
            throw new RuntimeException("Falha na geração de texto com detalhes", e);
        }
    }
}