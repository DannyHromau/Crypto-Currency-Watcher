package com.dannyhromau.watcher.api.external;


import com.dannyhromau.watcher.config.ExternalApiConfig;
import com.dannyhromau.watcher.exception.MethodNotAllowedException;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CoinLoreApiProvider implements CoinApiProvider {
    private final ExternalApiConfig externalApiConfig;

    private static final String api = "https://api.coinlore.net/api/ticker";
    private static final String apiParam = "/?id=";


    public Map<Integer, ExternalCoinDto> getExternalCoins() throws MethodNotAllowedException {
        String url = buildUrl(externalApiConfig, api, apiParam);
        Map<Integer, ExternalCoinDto> externalCoins = new HashMap<>();
        try {
            HttpEntity<String> response = restTemplate().exchange(url, HttpMethod.GET, getHttpEntity(), String.class);
            JSONArray jsonArray = new JSONArray(response.getBody());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                externalCoins.put(jsonObject.getInt("id"), new ExternalCoinDto(
                        jsonObject.getInt("id"),
                        jsonObject.getString("symbol"),
                        jsonObject.getString("name"),
                        jsonObject.getDouble("price_usd"),
                        ZonedDateTime.now()
                ));

            }
        } catch (JSONException e) {
            throw new MethodNotAllowedException("Cannot get JSON from url", url);
        }
        return externalCoins;
    }

    private static String buildUrl(ExternalApiConfig externalApiConfig, String api, String apiParam) {
        StringBuilder urlBuilder = new StringBuilder(api).append(apiParam);
        for (String id : externalApiConfig.getIds()) {
            urlBuilder.append(id).append(",");
        }
        return urlBuilder.substring(0, urlBuilder.toString().length() - 1);
    }

    @Bean
    private HttpEntity getHttpEntity() {
        return new HttpEntity<>(null);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
