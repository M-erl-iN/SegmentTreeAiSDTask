package SemesterAssignments._2_SegmentTree;
import java.util.Arrays;

public class SegmentTree {
    static class Node {
        int sum; int min; //ну или что-то для любых других операций типа max и тп
        Integer pendingVal = null;
        int from; int to; //ссылки на родителя|ребенка
        int size() {return to - from + 1;}
    }

    private Node[] heap;
    private int[] array;
    private int size;

    public SegmentTree(int[] array) {
        this.array = Arrays.copyOf(array, array.length);
        size = (int) (2 * Math.pow(2.0, Math.floor((Math.log((double) array.length) / Math.log(2.0)) + 1)));
        heap = new Node[size];
        build(1, 0, array.length);
    }

    public int size() {
        return array.length;
    }

    private void build(int v, int from, int size) {
        heap[v] = new Node();
        heap[v].from = from;
        heap[v].to = from + size - 1;

        if (size == 1) {
            heap[v].sum = array[from];
            heap[v].min = array[from];
        } else {
            //строим детишек()
            build(2 * v, from, size / 2);
            build(2 * v + 1, from + size / 2, size - size / 2);

            heap[v].sum = heap[2 * v].sum + heap[2 * v + 1].sum;
            //min = min среди детей
            heap[v].min = Math.min(heap[2 * v].min, heap[2 * v + 1].min);
        }
    }
    public int rangeSumQuery(int from, int to) {
        return rangeSumQuery(1, from, to);
    }

    private int rangeSumQuery(int v, int from, int to) {
        Node n = heap[v];

        //Если мы выполнили обновление диапазона, содержащее этот узел, мы можем вывести сумму, не спускаясь по дереву.
        if (n.pendingVal != null && contains(n.from, n.to, from, to)) {
            return (to - from + 1) * n.pendingVal;
        }

        if (contains(from, to, n.from, n.to)) {
            return heap[v].sum;
        }

        if (intersects(from, to, n.from, n.to)) {
            propagate(v);
            int leftSum = rangeSumQuery(2 * v, from, to);
            int rightSum = rangeSumQuery(2 * v + 1, from, to);

            return leftSum + rightSum;
        }

        return 0;
    }
    public int rangeMinQuery(int from, int to) {
        return rangeMinQuery(1, from, to);
    }

    private int rangeMinQuery(int v, int from, int to) {
        Node n = heap[v];


        //Если мы выполнили обновление диапазона, содержащее этот узел, мы можем вывести минимальное значение, не спускаясь по дереву.
        if (n.pendingVal != null && contains(n.from, n.to, from, to)) {
            return n.pendingVal;
        }

        if (contains(from, to, n.from, n.to)) {
            return heap[v].min;
        }

        if (intersects(from, to, n.from, n.to)) {
            propagate(v);
            int leftMin = rangeMinQuery(2 * v, from, to);
            int rightMin = rangeMinQuery(2 * v + 1, from, to);

            return Math.min(leftMin, rightMin);
        }

        return Integer.MAX_VALUE;
    }

    public void update(int from, int to, int value) {
        update(1, from, to, value);
    }

    private void update(int v, int from, int to, int value) {
        Node n = heap[v];

        if (contains(from, to, n.from, n.to)) {
            change(n, value);
        }

        if (n.size() == 1) return;

        if (intersects(from, to, n.from, n.to)) {
            propagate(v);

            update(2 * v, from, to, value);
            update(2 * v + 1, from, to, value);

            n.sum = heap[2 * v].sum + heap[2 * v + 1].sum;
            n.min = Math.min(heap[2 * v].min, heap[2 * v + 1].min);
        }
    }

    private void propagate(int v) {
        Node n = heap[v];

        if (n.pendingVal != null) {
            change(heap[2 * v], n.pendingVal);
            change(heap[2 * v + 1], n.pendingVal);
            n.pendingVal = null;
        }
    }

    private void change(Node n, int value) {
        n.pendingVal = value;
        n.sum = n.size() * value;
        n.min = value;
        array[n.from] = value;

    }

    private boolean contains(int from1, int to1, int from2, int to2) {
        return from2 >= from1 && to2 <= to1;
    }

    private boolean intersects(int from1, int to1, int from2, int to2) {
        return from1 <= from2 && to1 >= from2
                || from1 >= from2 && from1 <= to2;
    }
}
