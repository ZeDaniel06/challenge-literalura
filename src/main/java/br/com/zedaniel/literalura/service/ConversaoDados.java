package br.com.zedaniel.literalura.service;

import tools.jackson.databind.ObjectMapper;

public class ConversaoDados implements IConversaoDados{

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> T obterDados(String json, Class<T> classe) {
        return mapper.readValue(json, classe);
    }
}
