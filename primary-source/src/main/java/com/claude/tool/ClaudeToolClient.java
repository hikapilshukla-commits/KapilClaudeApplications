package com.claude.tool;
import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.core.JsonSchemaLocalValidation;
import com.anthropic.models.beta.messages.*;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.Model;
import io.github.cdimascio.dotenv.Dotenv;


import java.util.List;


public class ClaudeToolClient {




public static void main(String args[]){

    askToolAssistedMessageFromClaude();
}


  static  public void askToolAssistedMessageFromClaude() {

        Dotenv dotenv = Dotenv.load();
        String myKey = dotenv.get("ANTHROPIC_API_KEY");


// 2. Pass that specific string to the client builder
        AnthropicClient client = AnthropicOkHttpClient.builder()
                .apiKey(myKey) // This manually sets the x-api-key header
                .build();
        MessageCreateParams.Builder createParamsBuilder = MessageCreateParams.builder()
                .model(Model.CLAUDE_SONNET_4_5_20250929)
                .maxTokens(2048).system("You are a helpful assistant. Use the DateTimeTool to provide accurate time.") // Add thi
                .addTool(DateTimeTool.class, JsonSchemaLocalValidation.NO)
                .addUserMessage("What's time in Bangalore?");
      boolean keepRunning = true;
      BetaStopReason stopReason = null;
      BetaMessage response = null;
      while (keepRunning) {
          // Call the beta messages API
          response = client.beta().messages().create(createParamsBuilder.build());
          stopReason = response.stopReason().orElse(null);


          if (stopReason== null |stopReason.equals(BetaStopReason.TOOL_USE)) {


               response.content().stream()
                      .flatMap(contentBlock -> contentBlock.toolUse().stream())
                      .forEach(toolUseBlock -> createParamsBuilder
                              // Add a message indicating that the tool use was requested.
                              .addAssistantMessageOfBetaContentBlockParams(
                                      List.of(BetaContentBlockParam.ofToolUse(BetaToolUseBlockParam.builder()
                                              .name(toolUseBlock.name())
                                              .id(toolUseBlock.id())
                                              .input(toolUseBlock._input())
                                              .build())))
                              // Add a message with the result of the requested tool use.
                              .addUserMessageOfBetaContentBlockParams(
                                      List.of(BetaContentBlockParam.ofToolResult(BetaToolResultBlockParam.builder()
                                              .toolUseId(toolUseBlock.id())
                                              .contentAsJson(callTool(toolUseBlock))
                                              .build()))));


          }
          else {
              // 3. Final Answer reached (StopReason is usually END_TURN)
              response.content().forEach(block -> {
                  block.text().ifPresent(t -> System.out.println("Claude: " + t.text()));
              });

              keepRunning = false; // EXIT the loop
          }



    }
  }


    private static String  callTool(BetaToolUseBlock toolUseBlock)    {
        // 1. Check tool name (ensure it matches your registered name)
        if (!"date_time_tool".equals(toolUseBlock.name())) {
            throw new IllegalArgumentException("Unknown tool: " + toolUseBlock.name());
        }
        DateTimeTool tool = toolUseBlock.input(DateTimeTool.class);

        // 4. Execute tool logic


        try {
            return tool.getCurrentDateTime();
        } catch (Exception e) {
            // Return error to Claude so it can retry with a valid format
            return "Error: " + e.getMessage();
        }
    }

}
