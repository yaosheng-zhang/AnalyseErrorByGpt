package chatGPT;

import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import com.plexpt.chatgpt.util.Proxys;


import java.net.Proxy;
import java.util.Arrays;
import java.util.List;

public class Generator {

    private static final List<String> API_KEY_LIST=Arrays.asList(
//            "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",
//                                                             "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",
//                                                             "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",
//                                                            "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",
//                                                            "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",
//                                                            "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",
                                                            "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",
//                                                            "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",
//                                                            "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",
                                                            "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",
                                                            "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",
                                                             "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",
                                                            "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",
                                                            "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"


                                                            );
    private static final String API_HOST="https://api.openai.com/";


    private static final String SYSTEM="请你作为开发人员对代码进行重构，返回的结果中只允许出现重构后的代码，非代码部分必须以JAVA注释 // 的方式出现";





    public String  getCodeFromModle(String code){
        //国内需要代理 国外不需要

        Proxy proxy = Proxys.http("127.0.0.1", 1080);

        ChatGPT chatGPT = ChatGPT.builder()
                .apiKeyList(API_KEY_LIST)
                .proxy(proxy)
                .timeout(900)
                .apiHost(API_HOST) //反向代理地址
                .build()
                .init();

        Message system = getSystem();
        Message message = getMessage(code);

        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
                .messages(Arrays.asList(system, message))
                .temperature(0)
                .presencePenalty(0)
                .frequencyPenalty(0)
                .build();

        ChatCompletionResponse response = chatGPT.chatCompletion(chatCompletion);
        Message res = response.getChoices().get(0).getMessage();
        return res.getContent();
    }

    private Message getMessage(String code) {
        return Message.of(code);
    }

    private  Message getSystem() {
         return Message.ofSystem(SYSTEM);
    }



}
