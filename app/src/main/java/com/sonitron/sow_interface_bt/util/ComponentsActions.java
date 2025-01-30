package com.sonitron.sow_interface_bt.util;

// Classe abstrata base com implementações padrão
public abstract class ComponentsActions implements ComponetsActionsInterface {
    @Override
    public void OnCheckedChange() {
        // Implementação padrão (pode ser deixada vazia ou personalizada)
    }

    @Override
    public void OnCheckedNoChange() {
        // Implementação padrão (pode ser deixada vazia ou personalizada)
    }
    @Override
    public void onClick() {
        // Implementação padrão (pode ser deixada vazia ou personalizada)
    }
}
