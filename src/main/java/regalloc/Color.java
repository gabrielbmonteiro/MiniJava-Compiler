package regalloc;

import graph.Node;
import graph.NodeList;
import temp.Temp;
import temp.TempList;
import temp.TempMap;

import java.util.*;

public class Color implements TempMap {

    private InterferenceGraph ig;
    private TempMap initialAllocation; // Mapeamento de registradores já pré-coloridos
    private TempList registers;        // Lista dos registradores físicos livres para uso
    private int K;                     // Número de cores disponíveis

    private Map<Temp, Temp> colorMap = new HashMap<>(); // Resultado final: Temp -> Registrador
    private TempList spills = null;                     // Lista de Temps que precisaram de Spill

    private Stack<Node> selectStack = new Stack<>();
    private Set<Node> simplifyWorklist = new HashSet<>();
    private Set<Node> spillWorklist = new HashSet<>();
    private Map<Node, Integer> degree = new HashMap<>();

    public Color(InterferenceGraph ig, TempMap initial, TempList registers) {
        this.ig = ig;
        this.initialAllocation = initial;
        this.registers = registers;

        int count = 0;
        for (TempList r = registers; r != null; r = r.tail) count++;
        this.K = count;

        buildWorklists();
        makeWork();
        assignColors();
    }

    // Passo 1: Separar os nós entre os que podem ser simplificados e os que correm risco de spill
    private void buildWorklists() {
        for (NodeList nl = ig.nodes(); nl != null; nl = nl.tail) {
            Node n = nl.head;
            Temp t = ig.gtemp(n);

            // Ignora nós que já são (pré-coloridos)
            if (initialAllocation.tempMap(t) != null) {
                colorMap.put(t, t);
                continue;
            }

            int deg = n.degree();
            degree.put(n, deg);

            if (deg < K) {
                simplifyWorklist.add(n);
            } else {
                spillWorklist.add(n);
            }
        }
    }

    // Passo 2: Esvaziar as listas empilhando os nós
    private void makeWork() {
        while (!simplifyWorklist.isEmpty() || !spillWorklist.isEmpty()) {
            if (!simplifyWorklist.isEmpty()) {
                simplify();
            } else {
                spill();
            }
        }
    }

    private void simplify() {
        Node n = simplifyWorklist.iterator().next();
        simplifyWorklist.remove(n);

        selectStack.push(n);

        // Se um vizinho cair para grau < K, ele passa do spillWorklist para o simplifyWorklist
        for (NodeList adj = n.adj(); adj != null; adj = adj.tail) {
            Node m = adj.head;
            if (!selectStack.contains(m) && initialAllocation.tempMap(ig.gtemp(m)) == null) {
                int d = degree.get(m) - 1;
                degree.put(m, d);
                if (d == K - 1 && spillWorklist.contains(m)) {
                    spillWorklist.remove(m);
                    simplifyWorklist.add(m);
                }
            }
        }
    }

    private void spill() {
        Node spillNode = null;
        double minCost = Double.MAX_VALUE;

        // Heurística de Chaitin
        for (Node n : spillWorklist) {
            int cost = ig.spillCost(n);
            int deg = degree.get(n);

            double relativeCost = (deg > 0) ? (double) cost / deg : cost;

            if (relativeCost < minCost) {
                minCost = relativeCost;
                spillNode = n;
            }
        }

        spillWorklist.remove(spillNode);
        simplifyWorklist.add(spillNode);
    }

    // Passo 3: Desempilhar e colorir
    private void assignColors() {
        while (!selectStack.isEmpty()) {
            Node n = selectStack.pop();
            Temp t = ig.gtemp(n);

            // Coleta as cores que os vizinhos já estão usando
            Set<Temp> coloredNeighbors = new HashSet<>();
            for (NodeList adj = n.adj(); adj != null; adj = adj.tail) {
                Node m = adj.head;
                Temp neighborTemp = ig.gtemp(m);
                Temp neighborColor = initialAllocation.tempMap(neighborTemp) != null ?
                        neighborTemp : colorMap.get(neighborTemp);

                if (neighborColor != null) {
                    coloredNeighbors.add(neighborColor);
                }
            }

            // Procura a primeiro registrador disponível que não está nos vizinhos
            Temp selectedColor = null;
            for (TempList r = registers; r != null; r = r.tail) {
                if (!coloredNeighbors.contains(r.head)) {
                    selectedColor = r.head;
                    break;
                }
            }

            if (selectedColor != null) {
                colorMap.put(t, selectedColor);
            } else {
                // SPILL REAL! Não sobrou cor.
                spills = new TempList(t, spills);
            }
        }
    }

    public TempList spills() {
        return spills;
    }

    @Override
    public String tempMap(Temp t) {
        Temp color = colorMap.get(t);
        if (color != null) {
            String realName = initialAllocation.tempMap(color);
            return realName != null ? realName : color.toString();
        }
        return initialAllocation.tempMap(t); // Fallback
    }

}