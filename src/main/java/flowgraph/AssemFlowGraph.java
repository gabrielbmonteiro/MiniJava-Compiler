package flowgraph;

import assem.Instr;
import assem.InstrList;
import assem.LABEL;
import assem.MOVE;
import assem.OPER;
import assem.Targets;
import graph.Node;
import graph.NodeList;
import temp.Label;
import temp.TempList;
import temp.LabelList;

import java.util.HashMap;

public class AssemFlowGraph extends FlowGraph {

    private HashMap<Node, Instr> instrMap = new HashMap<>();
    private HashMap<Label, Node> labelMap = new HashMap<>();

    public AssemFlowGraph(InstrList instrs) {
        // 1ª: Criar os nós e preencher os mapas
        for (InstrList il = instrs; il != null; il = il.tail) {
            Instr instr = il.head;
            Node node = this.newNode();
            instrMap.put(node, instr);

            if (instr instanceof LABEL) {
                labelMap.put(((LABEL) instr).label, node);
            }
        }

        // 2ª: Adicionar as arestas
        Node prevNode = null;
        for (NodeList nl = this.nodes(); nl != null; nl = nl.tail) {
            Node currNode = nl.head;
            Instr currInstr = instrMap.get(currNode);

            // fall-through
            if (prevNode != null) {
                Instr prevInstr = instrMap.get(prevNode);
                // Adiciona aresta sequencial se a instrução anterior NÃO for um JUMP incondicional
                if (!isUnconditionalJump(prevInstr)) {
                    this.addEdge(prevNode, currNode);
                }
            }

            // Jumps
            Targets targets = currInstr.jumps();
            if (targets != null) {
                for (LabelList ll = targets.labels; ll != null; ll = ll.tail) {
                    Node targetNode = labelMap.get(ll.head);
                    if (targetNode != null) {
                        this.addEdge(currNode, targetNode);
                    }
                }
            }

            prevNode = currNode;
        }
    }

    private boolean isUnconditionalJump(Instr instr) {
        if (instr instanceof OPER) {
            String assem = ((OPER) instr).assem;
            return assem.trim().startsWith("j ");
        }
        return false;
    }

    @Override
    public TempList def(Node node) {
        return instrMap.get(node).def();
    }

    @Override
    public TempList use(Node node) {
        return instrMap.get(node).use();
    }

    @Override
    public boolean isMove(Node node) {
        return instrMap.get(node) instanceof MOVE;
    }

    public Instr instr(Node n) {
        return instrMap.get(n);
    }
}