package com.todo.todoapi.domain.enums;

public enum StatusTodoEnum {
    PLANEJADA(0, "Planejada"),
    EM_PROGRESSO(1, "Em progresso"),
    CONCLUIDO(2, "Concluído");

    private int codigo;
    private String descricao;

    StatusTodoEnum(int codigo, String descricao){
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}