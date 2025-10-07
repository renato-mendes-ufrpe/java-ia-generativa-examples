# 🤖 Exemplos de Integração com IA Generativa em Java

Projeto demonstrativo com **exemplos práticos** de integração com múltiplas plataformas de IA Generativa usando Java:

- 🟢 **Google Gemini** (via SDK oficial)  
- 🔵 **OpenAI GPT** (via SDK Java)

## 🎯 O que você vai encontrar

Este projeto contém **exemplos funcionais** que demonstram:

- ✅ **Chamadas básicas** para geração de texto
- ✅ **Integração com arquivos** (PDF, logs)
- ✅ **Sistema prático** de monitoramento de aquicultura
- ✅ **Análise comparativa** entre Gemini e OpenAI
- ✅ **Configuração multi-provider** flexível

## ⚡ Começar Rapidamente

### **Pré-requisitos:**
- Java 21 instalado
- Pelo menos uma API key (Gemini OU OpenAI)

### **Passos:**

1. **Obtenha o projeto:**
   
   **Opção A - Clone do repositório:**
   ```bash
   git clone https://github.com/renato-mendes-ufrpe/java-ia-generativa-examples.git
   cd java-ia-generativa-examples
   ```
   
   **Opção B - Download do arquivo ZIP:**
   ```bash
   # Baixe o ZIP do GitHub em: https://github.com/renato-mendes-ufrpe/java-ia-generativa-examples
   # Extraia o arquivo
   unzip java-ia-generativa-examples-main.zip
   cd java-ia-generativa-examples-main
   ```

2. **Configure as API keys** (veja seção de configuração abaixo)

3. **Execute um exemplo:**
   ```bash
   # Exemplos básicos com Gemini
   ./gradlew run
   
   # Integração com arquivos (PDF + logs)
   ./gradlew runFileIntegration
   
   # Monitoramento aquicultura - Gemini
   ./gradlew runAquaculture
   
   # Monitoramento aquicultura - OpenAI  
   ./gradlew runAquacultureOpenAI
   ```

## 📋 Requisitos Detalhados

### **Sistema:**
- **Java 21** (obrigatório - configurado no build.gradle)
- **Sistema operacional:** Windows, macOS, Linux
- **Conexão com internet** (para APIs)

### **APIs (pelo menos uma obrigatória):**
- **API Key Google Gemini** ([obter gratuitamente](https://aistudio.google.com/apikey))
- **API Key OpenAI** ([obter aqui](https://platform.openai.com/api-keys)) - *opcional*

### **Ferramentas (incluídas no projeto):**
- **Gradle Wrapper** - gerenciador de build (incluído)
- **Dependências Java** - baixadas automaticamente

## ⚙️ Configuração Completa (Do Zero)

### **1. Verificar Java**

Verifique se o Java está instalado:
```bash
java -version
```

Se não estiver instalado:
- **Windows:** Baixe do [site da Oracle](https://www.oracle.com/java/technologies/downloads/)
- **macOS:** `brew install openjdk@21` ou baixe do site
- **Linux:** `sudo apt install openjdk-21-jdk` (Ubuntu/Debian)

*💡 Sugestão avançada: Use [SDKMAN](https://sdkman.io/) para gerenciar múltiplas versões Java*

### **2. Obter API Keys**

#### **Google Gemini (Recomendado - Grátis):**
1. Acesse: https://aistudio.google.com/apikey
2. Clique em "Create API Key"
3. Copie a chave gerada (formato: `AIzaSy...`)

#### **OpenAI (Opcional - Pago):**
1. Acesse: https://platform.openai.com/api-keys
2. Crie uma conta e adicione créditos
3. Crie uma nova API key (formato: `sk-proj-...`)

### **3. Configurar Arquivo .env**

Copie o arquivo de exemplo e depois edite com suas chaves:
```bash
# Copie o template
cp .env.example .env

# Edite com suas chaves
nano .env  # ou use seu editor preferido
```

**Conteúdo do .env:**
```properties
# === GOOGLE GEMINI (Obrigatório para exemplos Gemini) ===
GOOGLE_API_KEY=AIzaSyA_sua_chave_completa_aqui
GEMINI_MODEL=gemini-2.0-flash-exp

# === OPENAI (Opcional - apenas para exemplos OpenAI) ===  
OPENAI_API_KEY=sk-proj-sua_chave_completa_aqui
OPENAI_MODEL=gpt-4o-mini
OPENAI_MAX_TOKENS=4096
OPENAI_TEMPERATURE=0.7

# === CONFIGURAÇÕES GERAIS ===
LOG_LEVEL=INFO
```

### **4. Verificar Configuração**

Teste se está funcionando:
```bash
# Compila o projeto
./gradlew build

# Executa exemplo básico
./gradlew run
```

Se aparecer "Demonstração concluída" sem erros, está configurado! ✅

## 🚀 Programas Disponíveis

### **1. Chamadas Básicas (Gemini)**
```bash
./gradlew run
```
**📋 O que faz:**
- Demonstra 3 tipos de interação com Gemini API
- Geração de texto simples com perguntas diretas
- Conversas contextuais combinando múltiplas mensagens
- Análise detalhada com múltiplos candidates e metadados

**🔧 Como usa a API:**
- **SDK**: `com.google.genai:google-genai:1.0.0`
- **Cliente**: `Client.builder().apiKey(apiKey).build()`
- **Método**: `client.models.generateContent(modelName, prompt, config)`
- **Configurações**: `GenerateContentConfig` para temperatura e candidates

```java
// Exemplo de código usado no programa
Client client = Client.builder()
    .apiKey(config.getGoogleApiKey())
    .build();

GenerateContentResponse response = client.models.generateContent(
    modelName, 
    prompt,
    null
);

String result = response.text();
```

---

### **2. Integração com Arquivos (Gemini)**  
```bash
./gradlew runFileIntegration
```
**📋 O que faz:**
- Upload real de PDF para análise via Gemini Files API
- Processa logs da aplicação automaticamente
- Gera relatórios estruturados baseados nos arquivos enviados
- Demonstra capacidade multimodal (texto + documentos)

**🔧 Como usa a API:**
- **Upload**: `UploadFileConfig` para enviar arquivos
- **Files API**: `client.files.upload()` com MIME type apropriado
- **Análise**: Combina arquivo enviado + prompt especializado
- **Processamento**: IA analisa conteúdo e retorna insights estruturados

```java
// Exemplo de código usado no programa
UploadFileConfig uploadConfig = UploadFileConfig.builder()
    .mimeType("application/pdf")
    .displayName("Resumo Estruturado do Projeto de Mestrado")
    .build();

File uploadedFile = client.files.upload(pdfPath, uploadConfig);

Content content = Content.builder()
    .parts(Arrays.asList(
        Part.fromText(prompt),
        Part.fromUri(uploadedFile.uri().orElse(""), uploadedFile.mimeType().orElse("application/pdf"))
    ))
    .build();

GenerateContentResponse response = client.models.generateContent(
    modelName,
    Arrays.asList(content),
    null
);
```

---

### **3. Monitoramento Aquicultura (Gemini)**
```bash
./gradlew runAquaculture
```
**📋 O que faz:**
- Simula sensores IoT realísticos (oxigênio, temperatura, pH, amônia)
- Gera 3 cenários: Normal, Alerta e Crítico
- Produz análises especializadas para tomada de decisão
- Fornece recomendações práticas de manejo aquícola

**🔧 Como usa a API:**
- **Dados estruturados**: Classe `SensorData` com parâmetros realísticos
- **Prompt especializado**: Template com conhecimento em aquicultura
- **Análise contextual**: IA recebe dados + parâmetros ideais para tilápia
- **Resposta estruturada**: Nível de risco + ações críticas + preventivas

```java
// Exemplo de código usado no programa
String prompt = buildAnalysisPrompt(sensorData);

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
```

---

### **4. Monitoramento Aquicultura (OpenAI)**
```bash
./gradlew runAquacultureOpenAI
```
**📋 O que faz:**
- Sistema idêntico ao anterior, usando OpenAI ChatCompletion
- Permite comparação direta entre provedores de IA
- Mesmo prompt e dados para análise comparativa justa
- Demonstra diferenças de resposta e estilo entre Gemini e OpenAI

**🔧 Como usa a API:**
- **SDK**: `com.openai:openai-java:4.2.0` (biblioteca oficial)
- **Cliente**: `OpenAIOkHttpClient.builder().apiKey()`
- **Chat Completion**: `ChatCompletionCreateParams` com mensagens
- **Modelo**: `gpt-4o-mini` com configurações de temperatura

```java
// Exemplo de código usado no programa
OpenAIClient client = OpenAIOkHttpClient.builder()
    .apiKey(config.getOpenaiApiKey())
    .build();

ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
    .addSystemMessage("Você é um especialista em aquicultura com vasta experiência em monitoramento de viveiros de tilápia.")
    .addUserMessage(prompt)
    .model(ChatModel.of(config.getOpenaiModel()))
    .maxCompletionTokens(config.getOpenaiMaxTokens())
    .temperature(config.getOpenaiTemperature())
    .build();

ChatCompletion completion = client.chat().completions().create(params);
String response = completion.choices()
    .get(0)
    .message()
    .content()
    .orElse("Resposta vazia do OpenAI");
```

## 📊 Exemplo de Saída
```
=== DEMONSTRAÇÃO DE CHAMADAS BÁSICAS - GEMINI API ===

--- Exemplo 1: Pergunta simples ---
Resposta: Inteligência Artificial é um campo da ciência...

--- Exemplo 2: Conversa com contexto ---
Resposta: Para iniciantes em Java, recomendo focar em...

--- Exemplo 3: Resposta com detalhes técnicos ---
=== RESPOSTA DETALHADA COM MÚLTIPLOS CANDIDATES ===
Número de candidatos gerados: 2

--- CANDIDATE 1 ---
Texto: ArrayList usa array dinâmico...
Razão de finalização: STOP

--- CANDIDATE 2 ---
Texto: LinkedList usa lista duplamente encadeada...
Razão de finalização: STOP

=== INFORMAÇÕES DE TOKENS E METADATA ===
Tokens de entrada: 12
Tokens de saída: 1578
Total de tokens: 1590
```

## 📁 Estrutura

```
src/main/java/com/example/
├── config/
│   └── AppConfig.java              # Configuração compartilhada (.env)
├── gemini/
│   ├── Main.java                   # Demonstrações básicas Gemini
│   ├── MainFileIntegration.java    # Integração com arquivos
│   ├── MainAquaculture.java        # Sistema aquicultura Gemini
│   ├── GeminiBasicExample.java     # Métodos básicos API Gemini
│   ├── GeminiFileExample.java      # Upload e análise arquivos
│   ├── AquacultureMonitoringExample.java  # Sistema IoT completo
│   └── SensorData.java             # Estrutura de dados sensores
└── openai/
    ├── MainAquacultureOpenAI.java   # Sistema aquicultura OpenAI
    ├── AquacultureOpenAIExample.java # Implementação OpenAI
    └── SensorData.java              # Mesma estrutura dados

src/main/resources/
├── resumo_estruturado_projeto.pdf  # Arquivo base para análises
├── exemplo_documento.txt           # Documento de exemplo
└── logback.xml                     # Configuração de logs

.env                           # Configurações (criar baseado em .env.example)
```

## 🔧 Classes Principais

### `AppConfig` - Configuração Compartilhada
- **Localização:** `com.example.config.AppConfig`
- Carrega automaticamente o arquivo `.env`
- Suporta múltiplos provedores (Gemini + OpenAI)
- Validação de configurações por provedor
- Singleton pattern para acesso global

### `GeminiBasicExample` - Chamadas Básicas Gemini
- `generateSimpleText(prompt)` - Geração básica
- `generateWithContext(messages...)` - Conversa contextual
- `generateWithDetails(prompt)` - Com metadados completos

### `GeminiFileExample` - Integração com Arquivos Gemini
- `analyzeProjectWithGemini()` - Faz upload de PDF do projeto e analisa com IA
- `analyzeLogFile()` - Faz upload do log da aplicação e gera relatório detalhado

### `AquacultureMonitoringExample` - Sistema IoT Gemini
- `generateSimulatedData(pondId, scenario)` - Simula dados de sensores com cenários
- `analyzeWithGemini(sensorData)` - Análise completa com Gemini API
- `runMonitoringDemo()` - Demonstração completa com 3 cenários (normal/alerta/crítico)

### `AquacultureOpenAIExample` - Sistema IoT OpenAI
- `generateSimulatedData(pondId, scenario)` - Simula dados de sensores com cenários
- `analyzeWithOpenAI(sensorData)` - Análise completa com OpenAI ChatCompletion
- `runMonitoringDemo()` - Demonstração completa com 3 cenários (normal/alerta/crítico)

## 💡 Código de Exemplo

```java
// Instância configurada automaticamente via .env
GeminiBasicExample example = new GeminiBasicExample();

// Pergunta simples
String response = example.generateSimpleText("O que é IA?");

// Conversa contextual
String conversation = example.generateWithContext(
    "Sou iniciante em Java",
    "Preciso de dicas",
    "Por onde começar?"
);

// Análise detalhada com 2 candidates
example.generateWithDetails("ArrayList vs LinkedList");
```

## 🔍 Recursos Técnicos

### **SDKs Utilizados:**
- **Google Gemini**: `com.google.genai:google-genai:1.0.0`
- **OpenAI**: `com.openai:openai-java:4.2.0`

### **Funcionalidades:**
- **Multi-provider**: Suporte simultâneo Gemini + OpenAI
- **Configuração centralizada**: AppConfig com validação automática
- **Upload de arquivos**: Análise de PDF e logs via Files API
- **Logging detalhado**: SLF4J + Logback com rastreamento completo
- **Simulação IoT**: Dados realistas para monitoramento aquicultura
- **Environment seguro**: Configurações via arquivo .env

## 🐛 Soluções Comuns

| Problema | Solução |
|----------|---------|
| `GOOGLE_API_KEY não encontrada` | Configure no `.env`: `GOOGLE_API_KEY=AIzaSy...` |
| `OPENAI_API_KEY não encontrada` | Configure no `.env`: `OPENAI_API_KEY=sk-proj...` |
| `Java version incompatible` | Instale Java 21: `brew install openjdk@21` |
| `Build failed` | Execute: `./gradlew clean build` |
| `Permission denied: ./gradlew` | Execute: `chmod +x gradlew` |
| `Rate limit exceeded (Gemini)` | Aguarde 1 minuto ou use API key válida |
| `Insufficient quota (OpenAI)` | Adicione créditos na plataforma OpenAI |

## 🔗 Links

- 🏠 [Repositório GitHub](https://github.com/renato-mendes-ufrpe/java-ia-generativa-examples)
- 📖 [Documentação Gemini API](https://ai.google.dev/gemini-api/docs)
- � [Documentação OpenAI API](https://platform.openai.com/docs/api-reference)
- 🔑 [Gemini API Keys](https://aistudio.google.com/apikey)
- 🔑 [OpenAI API Keys](https://platform.openai.com/api-keys)
- � [Preços Gemini](https://ai.google.dev/gemini-api/docs/pricing)
- � [Preços OpenAI](https://openai.com/api/pricing/)
- 📚 [SDK Gemini Java](https://github.com/googleapis/google-genai-java)
- 📚 [SDK OpenAI Java](https://github.com/openai/openai-java)

---

**🎯 Projeto focado em demonstração prática e comparação entre múltiplas plataformas de IA Generativa em Java.**