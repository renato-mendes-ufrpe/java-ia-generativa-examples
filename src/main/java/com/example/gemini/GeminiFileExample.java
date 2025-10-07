package com.example.gemini;

import com.example.config.AppConfig;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.UploadFileConfig;
import com.google.genai.types.File;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Integração com arquivos usando Gemini Files API.
 * 
 * @author Renato V. Mendes
 */
public class GeminiFileExample {
    
    private static final Logger logger = LoggerFactory.getLogger(GeminiFileExample.class);
    private final AppConfig config;
    private final String modelName;
    private final Client client;
    
    public GeminiFileExample() {
        // Carrega as configurações do .env
        this.config = AppConfig.getInstance();
        this.modelName = config.getGeminiModel();
        
        // Verifica se a API key está disponível
        String apiKey = config.getGoogleApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("GOOGLE_API_KEY não encontrada no arquivo .env");
        }
        
        // Configura o cliente
        this.client = Client.builder()
            .apiKey(apiKey)
            .build();
        
        logger.info("GeminiFileExample inicializado com modelo: {}", modelName);
    }
    

    
    /**
     * Faz upload de PDF e analisa com Gemini.
     * @return análise do projeto em formato texto
     */
    public String analyzeProjectWithGemini() {
        try {
            logger.info("Iniciando análise de projeto com upload real do PDF");
            
            // Caminho para o arquivo PDF
            String pdfPath = "src/main/resources/resumo_estruturado_projeto.pdf";
            Path path = Paths.get(pdfPath);
            
            String prompt = """
                Com base no resumo estruturado do projeto em anexo (arquivo PDF), faça uma proposta de alto nível de como seria 
                a integração dessa solução com a API do Gemini e me dê um exemplo de implementação com Java (exemplo simples).
                
                Por favor, inclua:
                1. Proposta de integração de alto nível
                2. Benefícios específicos para o projeto educacional  
                3. Exemplo simples de implementação em Java
                """;
            
            if (!Files.exists(path)) {
                logger.warn("Arquivo PDF não encontrado em: {}. Usando prompt sem arquivo.", pdfPath);
                
                // Fallback: envia apenas o prompt sem arquivo
                GenerateContentResponse response = client.models.generateContent(
                    modelName,
                    prompt + "\n\nNOTA: Arquivo PDF não encontrado. Análise baseada em conhecimento geral sobre sistemas educacionais.",
                    null
                );
                
                String result = response.text();
                logger.info("Análise completada (sem PDF) com {} caracteres", result.length());
                return result;
            }
            
            logger.info("PDF encontrado. Fazendo upload para a Files API...");
            
            // Configura o upload do arquivo PDF
            UploadFileConfig uploadConfig = UploadFileConfig.builder()
                .mimeType("application/pdf")
                .displayName("Resumo Estruturado do Projeto de Mestrado")
                .build();
            
            // Faz o upload real do PDF
            File uploadedFile = client.files.upload(pdfPath, uploadConfig);
            logger.info("Upload concluído! Arquivo: {}", uploadedFile.name().orElse("sem nome"));
            
            // Log do prompt textual que será enviado
            logger.info("Prompt textual que será enviado:");
            logger.info("--- INÍCIO DO PROMPT ---");
            logger.info("{}", prompt);
            logger.info("--- FIM DO PROMPT ---");
            
            // Cria o conteúdo com texto + arquivo PDF
            Content content = Content.builder()
                .parts(Arrays.asList(
                    Part.fromText(prompt),
                    Part.fromUri(uploadedFile.uri().orElse(""), uploadedFile.mimeType().orElse("application/pdf"))
                ))
                .build();
            
            logger.info("Enviando requisição para Gemini com PDF anexado...");
            
            // Envia para o Gemini com o arquivo anexado
            GenerateContentResponse response = client.models.generateContent(
                modelName,
                Arrays.asList(content),
                null
            );
            
            String result = response.text();
            logger.info("Análise completada (com PDF) com {} caracteres", result.length());
            
            // Opcional: limpar o arquivo após uso (fica na API por 48h automaticamente)
            try {
                client.files.delete(uploadedFile.name().orElse(""), null);
                logger.info("Arquivo removido da Files API após uso");
            } catch (Exception e) {
                logger.warn("Não foi possível remover o arquivo da API: {}", e.getMessage());
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("Erro ao analisar projeto com Gemini: {}", e.getMessage(), e);
            throw new RuntimeException("Falha na análise do projeto", e);
        }
    }
    
    /**
     * Analisa arquivo de log da aplicação com Gemini.
     * @return relatório de análise dos logs
     */
    public String analyzeLogFile() {
        try {
            logger.info("Iniciando análise do arquivo de log da aplicação...");
            
            // Caminho para o arquivo de log da aplicação
            String logPath = "logs/gemini-app.log";
            Path logFilePath = Paths.get(logPath);
            
            // Verifica se o arquivo de log existe
            if (!Files.exists(logFilePath)) {
                logger.warn("Arquivo de log não encontrado: {}", logPath);
                return "❌ Arquivo de log não encontrado. Execute algumas operações primeiro para gerar logs.";
            }
            
            logger.info("Arquivo de log encontrado: {}", logFilePath.toAbsolutePath());
            
            // Prompt especializado para análise de logs
            String prompt = """
                Você é um especialista em análise de logs de aplicações Java.
                
                Analise o arquivo de log anexado e forneça um relatório detalhado sobre:
                
                📊 **RESUMO EXECUTIVO:**
                - Período de tempo coberto pelos logs
                - Número total de eventos/operações
                - Status geral da aplicação (saudável/problemas)
                
                ⚠️ **ANÁLISE DE PROBLEMAS:**
                - Erros críticos encontrados
                - Warnings que merecem atenção
                - Exceções e stack traces
                - Problemas de performance
                
                📈 **PADRÕES DE COMPORTAMENTO:**
                - Operações mais frequentes
                - Horários de pico de atividade
                - Fluxos de execução identificados
                
                🔧 **RECOMENDAÇÕES:**
                - Melhorias de logging
                - Pontos de atenção para monitoramento
                - Sugestões de otimização
                - Alertas para configurar
                
                🚨 **ALERTAS DE SEGURANÇA:**
                - Tentativas de acesso suspeitas
                - Informações sensíveis expostas
                - Padrões anômalos
                
                Seja específico e prático. Foque em insights acionáveis para desenvolvedores.
                """;
            
            // Log do prompt textual que será enviado
            logger.info("Prompt para análise de log:");
            logger.info("--- INÍCIO DO PROMPT ---");
            logger.info("{}", prompt);
            logger.info("--- FIM DO PROMPT ---");
            
            // Configuração de upload do arquivo de log
            UploadFileConfig uploadConfig = UploadFileConfig.builder()
                .mimeType("text/plain")
                .displayName("gemini-app.log")
                .build();
            
            // Faz o upload real do arquivo de log
            java.io.File logFile = logFilePath.toFile();
            File uploadedFile = client.files.upload(logFile, uploadConfig);
            logger.info("Upload do log concluído! Arquivo: {}", uploadedFile.name().orElse("sem nome"));
            
            // Cria o conteúdo com texto + arquivo de log
            Content content = Content.builder()
                .parts(Arrays.asList(
                    Part.fromText(prompt),
                    Part.fromUri(uploadedFile.uri().orElse(""), uploadedFile.mimeType().orElse("text/plain"))
                ))
                .build();
            
            logger.info("Enviando requisição para Gemini com arquivo de log anexado...");
            
            // Envia para o Gemini com o arquivo anexado
            GenerateContentResponse response = client.models.generateContent(
                modelName,
                Arrays.asList(content),
                null
            );
            
            String result = response.text();
            logger.info("Análise de log completada com {} caracteres", result.length());
            
            // Limpa o arquivo da API após uso
            try {
                client.files.delete(uploadedFile.name().orElse(""), null);
                logger.info("Arquivo de log removido da Files API após uso");
            } catch (Exception e) {
                logger.warn("Não foi possível remover o arquivo de log da API: {}", e.getMessage());
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("Erro ao analisar arquivo de log: {}", e.getMessage(), e);
            throw new RuntimeException("Falha na análise do arquivo de log", e);
        }
    }
}