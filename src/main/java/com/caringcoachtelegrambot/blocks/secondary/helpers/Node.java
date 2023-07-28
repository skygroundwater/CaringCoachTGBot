package com.caringcoachtelegrambot.blocks.secondary.helpers;

import com.caringcoachtelegrambot.blocks.parents.Blockable;
import lombok.Data;

@Data
public class Node {

    private Blockable prevBlockable;

    private Blockable nextBlockable;

}
