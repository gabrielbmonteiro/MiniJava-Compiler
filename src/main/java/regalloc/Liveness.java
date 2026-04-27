package regalloc;

import flowgraph.FlowGraph;
import graph.Node;
import graph.NodeList;
import temp.Temp;
import temp.TempList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Liveness extends InterferenceGraph {

    private FlowGraph cfg;
    private Map<Node, Set<Temp>> liveInMap = new HashMap<>();
    private Map<Node, Set<Temp>> liveOutMap = new HashMap<>();

    private Map<Temp, Node> tempNodeMap = new HashMap<>();
    private Map<Node, Temp> nodeTempMap = new HashMap<>();
    private MoveList moveList = null;

    private Map<Temp, Integer> useDefCount = new HashMap<>();

    public Liveness(FlowGraph cfg) {
        this.cfg = cfg;
        buildLiveness();
        buildInterferenceGraph();
    }

    private void buildLiveness() {
        for (NodeList nl = cfg.nodes(); nl != null; nl = nl.tail) {
            Node n = nl.head;

            for (TempList defs = cfg.def(n); defs != null; defs = defs.tail) {
                useDefCount.put(defs.head, useDefCount.getOrDefault(defs.head, 0) + 1);
            }
            for (TempList uses = cfg.use(n); uses != null; uses = uses.tail) {
                useDefCount.put(uses.head, useDefCount.getOrDefault(uses.head, 0) + 1);
            }

            liveInMap.put(n, new HashSet<>());
            liveOutMap.put(n, new HashSet<>());
        }

        boolean changed = true;

        while (changed) {
            changed = false;

            for (NodeList nl = cfg.nodes(); nl != null; nl = nl.tail) {
                Node n = nl.head;

                // Cópias dos conjuntos da iteração anterior
                Set<Temp> inPrime = new HashSet<>(liveInMap.get(n));
                Set<Temp> outPrime = new HashSet<>(liveOutMap.get(n));

                // Calcula novo out[n]: União dos in[s] de todos os sucessores s
                Set<Temp> newOut = new HashSet<>();
                for (NodeList succ = n.succ(); succ != null; succ = succ.tail) {
                    newOut.addAll(liveInMap.get(succ.head));
                }
                liveOutMap.put(n, newOut);

                // Calcula novo in[n]: use[n] U (out[n] - def[n])
                Set<Temp> newIn = tempListToSet(cfg.use(n));
                Set<Temp> outMinusDef = new HashSet<>(newOut);
                outMinusDef.removeAll(tempListToSet(cfg.def(n)));
                newIn.addAll(outMinusDef);
                liveInMap.put(n, newIn);

                // Verifica se houve alguma mudança nessa iteração
                if (!newIn.equals(inPrime) || !newOut.equals(outPrime)) {
                    changed = true;
                }
            }
        }
    }

    private Set<Temp> tempListToSet(TempList tl) {
        Set<Temp> set = new HashSet<>();
        for (TempList current = tl; current != null; current = current.tail) {
            if (current.head != null) {
                set.add(current.head);
            }
        }
        return set;
    }

    private void buildInterferenceGraph() {
        for (NodeList nl = cfg.nodes(); nl != null; nl = nl.tail) {
            Node n = nl.head;
            for (TempList tl = cfg.def(n); tl != null; tl = tl.tail) {
                getOrCreateNode(tl.head);
            }
            for (TempList tl = cfg.use(n); tl != null; tl = tl.tail) {
                getOrCreateNode(tl.head);
            }
        }

        for (NodeList nl = cfg.nodes(); nl != null; nl = nl.tail) {
            Node n = nl.head;
            Set<Temp> liveOut = getLiveOut(n);
            boolean isMove = cfg.isMove(n);

            TempList defs = cfg.def(n);
            TempList uses = cfg.use(n);

            Set<Temp> moveUses = new HashSet<>();
            if (isMove && uses != null) {
                for (TempList u = uses; u != null; u = u.tail) {
                    moveUses.add(u.head);
                }
            }

            for (TempList d = defs; d != null; d = d.tail) {
                Temp defTemp = d.head;
                Node defNode = getOrCreateNode(defTemp);

                for (Temp liveTemp : liveOut) {
                    if (defTemp == liveTemp) continue;

                    // Se for um MOVE (a <- c) e 'liveTemp' for igual a 'c', não há interferência.
                    if (isMove && moveUses.contains(liveTemp)) {
                        continue;
                    }

                    Node liveNode = getOrCreateNode(liveTemp);

                    this.addEdge(defNode, liveNode);
                    this.addEdge(liveNode, defNode);
                }
            }
        }
    }

    private Node getOrCreateNode(Temp t) {
        if (t == null) return null;

        Node n = tnode(t);
        if (n == null) {
            n = this.newNode();
            tempNodeMap.put(t, n);
            nodeTempMap.put(n, t);
        }
        return n;
    }

    @Override
    public Node tnode(Temp temp) {
        return tempNodeMap.get(temp);
    }

    @Override
    public Temp gtemp(Node node) {
        return nodeTempMap.get(node);
    }

    @Override
    public MoveList moves() {
        return moveList;
    }

    @Override
    public int spillCost(Node node) {
        Temp t = gtemp(node);
        return useDefCount.getOrDefault(t, 1);
    }

    public Set<Temp> getLiveOut(Node n) {
        return liveOutMap.get(n);
    }
}