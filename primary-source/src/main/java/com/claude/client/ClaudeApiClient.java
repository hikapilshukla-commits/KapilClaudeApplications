package com.claude.client;
import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

public class ClaudeApiClient {


static ArrayList<String> chatMessages = new ArrayList<>();
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter text (type 'exit' to stop):");

        // Loop to collect and print strings one by one
        while (scanner.hasNextLine()) {
            String message = scanner.nextLine(); // Read the next line

            // Check for a termination condition
            if (message.equalsIgnoreCase("exit")) {
                break;
            }
            String chatmessage = createChatMessage(message);
            String Response =    sendMessageToClaude(chatmessage);
            // Print the result back immediately
            System.out.println("Here is your answer from Claude: " + Response);
        }

        scanner.close(); // Recommended to close the scanner after use






}
private static  String  createChatMessage(String newMessage){
    StringBuffer chatMessage = new StringBuffer();
         chatMessages.add(newMessage.toString());
         for(String message : chatMessages){


             chatMessage.append(message);
         }
        return chatMessage.toString();

}
    private static String sendMessageToClaude(String newmessage){

        Dotenv dotenv = Dotenv.load();
        String myKey = dotenv.get("ANTHROPIC_API_KEY");
// 2. Pass that specific string to the client builder
        AnthropicClient client = AnthropicOkHttpClient.builder()
                .apiKey(myKey) // This manually sets the x-api-key header
                .build();
        MessageCreateParams params = MessageCreateParams.builder()
                .maxTokens(1024L).system("You are a Expert Chat application on various topics, GIve short and Chat like Answer")
                .addUserMessage(newmessage)
                .model(Model.CLAUDE_SONNET_4_5)
                .build();

        Message message = client.messages().create(params);
        return message.content().get(0).text().get().text();
    }


}