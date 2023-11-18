import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

public class TP2 {
    private static InputReader in;
    private static PrintWriter out;
    static int totalClasses, totalQueries;
    static int[] classCapacity;
    static LinkedList studentRankingEachClass = new LinkedList();
    static ArrayList<Integer> studentPoints = new ArrayList<Integer>();
    static ArrayList<Integer> cheatingCounter = new ArrayList<Integer>();
    static int idCounter = 0;

    public static void main(String[] args) throws IOException {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        totalClasses = in.nextInt(); //banyaknya kelas pada sekolah 
        classCapacity = new int[totalClasses + 5]; //tempat penyimpanan banyaknya siswa dikelas dan dijamin >5

        int i = 1;
        while (i <= totalClasses) {
            classCapacity[i] = in.nextInt(); //input banyaknya siswa per-kelas
            i++;
        }

        int j = 1;
        while (j <= totalClasses) {  //banyaknya siswa perkelas 
            int k = 1;
            nodeForLinkedList currentClass = studentRankingEachClass.addBack(j); //masukkin kedalam linkedList kelas-kelas  tsb.
            while (k <= classCapacity[j]) {
                int p = in.nextInt(); // minta point sesuai jumlah kapasitas kelas 
                studentPoints.add(p); // terus point di kapasitas kelas itu masukkin ke dalam student point 
                cheatingCounter.add(0); //dalam arrayList cheating counter tambahin 0 semua karena belum ada yang curang
                idCounter++; // id nya ditambahin untuk setiap siswa 
                currentClass.jumlah += p; // masukkin penjumlahan poin dari kelas yang ada 
                currentClass.tree.root = currentClass.tree.insert(currentClass.tree.root, p, -idCounter); //root dari kelas yang di insert jadi dia dalam tree.

                k++;
            }
            j++;
        }

        totalQueries = in.nextInt(); //input banyaknya perintah 
        int l = 1;
        while (l <= totalQueries) { // loop sejumlah banyaknya perintah 
            String type = in.next();
            if ("T".equals(type)) {
                queryT();
            } else if ("C".equals(type)) {
                queryC();
            } else if ("G".equals(type)) {
                queryG();
            } else if ("S".equals(type)) {
                queryS();
            } else if ("K".equals(type)) {
                queryK();
            } else if ("A".equals(type)) {
                queryA();
            }
            l++;
        }
        out.flush();
        out.close();

        // Assume "tree" is an instance of your Tree class and "root" is the root node of your tree
        

    }

    

    private static void queryK() {
        studentRankingEachClass.insertionSort(); //kelasnya disort 
        nodeForLinkedList currentNode = studentRankingEachClass.head.next;
        int counter = 1;
        for (; currentNode.id != studentRankingEachClass.current.id; currentNode = currentNode.next) {
            counter++;
        }
        out.println(counter);

    }

    private static void queryA() {
        int n = in.nextInt();
        nodeForLinkedList currentClass = studentRankingEachClass.addBack(++totalClasses);
        int i = 1;
        while (i <= n) {
            int p = 0;
            studentPoints.add(p);
            cheatingCounter.add(0);
            idCounter++;
            currentClass.jumlah += p;
            currentClass.tree.root = currentClass.tree.insert(currentClass.tree.root, p, -idCounter);
            i++ ;
        }
        out.println(totalClasses);
    }

    private static void queryG() {
        String movementType = in.next();
        if ("R".equals(movementType)){
            studentRankingEachClass.moveRight();

        }if ("L".equals(movementType)){
            studentRankingEachClass.moveLeft();
        }
        out.println(studentRankingEachClass.current.id);
    }

    private static void queryT() {
        int poin = in.nextInt();
        int id = in.nextInt();

        if (doesStudentExist(id)) {
            out.println("-1");
            return;
        }

        int currentPoin = studentPoints.get(id - 1);

        Node resultNode = studentRankingEachClass.current.tree.findNode(studentRankingEachClass.current.tree.root,
                currentPoin, -id);

        if (resultNode != null) {
            int newPoin = currentPoin + poin + Math.min(poin, studentRankingEachClass.current.tree
                    .amountOfTutor(studentRankingEachClass.current.tree.root, currentPoin) - 1);

            studentRankingEachClass.current.tree.root = studentRankingEachClass.current.tree
                    .delete(studentRankingEachClass.current.tree.root, currentPoin, -id, 0);

            studentRankingEachClass.current.jumlah -= currentPoin;
            studentRankingEachClass.current.tree.root = studentRankingEachClass.current.tree
                    .insert(studentRankingEachClass.current.tree.root, newPoin, -id);

            studentRankingEachClass.current.jumlah += newPoin;
            studentPoints.set(id - 1, newPoin);
            out.println(newPoin);

        } else {
            out.println("-1");
        }
    }

    private static boolean doesStudentExist(int id) {
        return id - 1 >= studentPoints.size();
    }

    private static void queryS() {
        Tree current = studentRankingEachClass.current.tree;
        Tree next = null;
        Tree prev = null;
        if(studentRankingEachClass.current.next.id != -1) next = studentRankingEachClass.current.next.tree;
        if(studentRankingEachClass.current.prev.id != -1) prev = studentRankingEachClass.current.prev.tree;
        if(studentRankingEachClass.size == 1){
            out.println("-1 -1");
            return;
        }

        int kasus = 0;
        if(next == null) kasus = 1;
        else if(prev == null) kasus = 2;

        ArrayList<Student> minCurrent = new ArrayList<Student>();
        ArrayList<Student> maxCurrent =new ArrayList<Student>();

        ArrayList<Student> minPrev = new ArrayList<Student>();
        ArrayList<Student> maxNext = new ArrayList<Student>();

        int i = 0;
        while(i<3){
            if(kasus == 2 || kasus == 0){
                Student resMinCurrent = current.minValueNode(current.root,-1,1);
                current.root = current.delete(current.root,resMinCurrent.poin, resMinCurrent.id,0);
                studentRankingEachClass.current.jumlah -= resMinCurrent.poin;

                minCurrent.add(resMinCurrent);

                Student resMaxNext = next.maxValueNode(next.root,-1,1);
                next.root = next.delete(next.root, resMaxNext.poin, resMaxNext.id,0);
                studentRankingEachClass.current.next.jumlah -= resMaxNext.poin;

                maxNext.add(resMaxNext);
            }
            if(kasus ==1 || kasus == 0){
                Student resMaxCurrent = current.maxValueNode(current.root,-1,1);
                current.root = current.delete(current.root,resMaxCurrent.poin,resMaxCurrent.id,0);
                studentRankingEachClass.current.jumlah -= resMaxCurrent.poin;

                maxCurrent.add(resMaxCurrent);

                Student resMinPrev = prev.minValueNode(prev.root,-1,1);
                prev.root = prev.delete(prev.root, resMinPrev.poin, resMinPrev.id,0);
                studentRankingEachClass.current.prev.jumlah -= resMinPrev.poin;

                minPrev.add(resMinPrev);
            }

            
            i++;
        }

        int j =0;
        while(j < 3){
            if(kasus ==2 || kasus == 0){
                Student resMinCurrent = minCurrent.get(j);
                Student resMaxNext = maxNext.get(j);

                next.root = next.insert(next.root, resMinCurrent.poin, resMinCurrent.id);
                studentRankingEachClass.current.jumlah += resMaxNext.poin;
            }

            if(kasus ==1|| kasus ==0){
                Student resMaxCurrent = maxCurrent.get(j);
                Student resMinPrev = minPrev.get(j);

                prev.root = prev.insert(prev.root, resMaxCurrent.poin,resMaxCurrent.id);
                studentRankingEachClass.current.prev.jumlah += resMaxCurrent.poin;

                current.root= current.insert(current.root,resMinPrev.poin, resMinPrev.id);
                studentRankingEachClass.current.jumlah += resMinPrev.poin;
            }
            j++;
        }

        Student minPoin = current.minValueNode(current.root,-1,1);
        Student maxPoin = current.maxValueNode(current.root,-1,1);

        current.printTree(current.root, "", false);
        next.printTree(next.root, "", false);
        //prev.printTree(prev.root, "", false);

        //out.println(-maxPoin.poin + " "+ -minPoin.poin);
    }
        

    private static void queryC() {
        int id = in.nextInt();

        if (doesStudentExist(id)) {
            out.println("-1");
            return;
        }

        int index = id - 1;
        int poin = studentPoints.get(index);
        int penalty = cheatingCounter.get(index);

        Node resultNode = studentRankingEachClass.current.tree.findNode(studentRankingEachClass.current.tree.root, poin,
                -id);

        if (checkNode(resultNode)) {
            if (penalty == 0) {
                studentRankingEachClass.current.tree.root = studentRankingEachClass.current.tree
                        .delete(studentRankingEachClass.current.tree.root, poin, -id, 0);

                studentRankingEachClass.current.jumlah -= poin;
                studentRankingEachClass.current.tree.root = studentRankingEachClass.current.tree
                        .insert(studentRankingEachClass.current.tree.root, 0, -id);
                studentRankingEachClass.current.jumlah += 0;

                studentPoints.set(index, 0);
                cheatingCounter.set(index, 1);
                out.println("0");

            } else if (penalty == 1) {
                studentRankingEachClass.current.tree.root = studentRankingEachClass.current.tree
                        .delete(studentRankingEachClass.current.tree.root, poin, -id, 0);

                studentRankingEachClass.current.jumlah -= poin;

                studentRankingEachClass.tail.prev.tree.root = studentRankingEachClass.tail.prev.tree
                        .insert(studentRankingEachClass.tail.prev.tree.root, 0, -id);
                studentRankingEachClass.tail.prev.jumlah += 0;

                studentPoints.set(index, 0);
                cheatingCounter.set(index, 2);

                if (checkRanking()) {
                    Tree dari = studentRankingEachClass.current.tree;
                    Tree ke = studentRankingEachClass.current.next.tree;

                    studentRankingEachClass.current.next.jumlah += studentRankingEachClass.current.jumlah;
                    studentRankingEachClass.current = studentRankingEachClass.current.next;
                    studentRankingEachClass.current.prev = studentRankingEachClass.current.prev.prev;

                    dari.move(dari.root, -1, 1, ke);
                }
                out.println(studentRankingEachClass.tail.prev.id);
            } else if (penalty == 2) {
                studentRankingEachClass.current.tree.root = studentRankingEachClass.current.tree
                        .delete(studentRankingEachClass.current.tree.root, poin, -id, 0);
                studentRankingEachClass.current.jumlah -= poin;

                cheatingCounter.set(index, 3);

                if (checkRanking()) {
                    Tree from = studentRankingEachClass.current.tree;
                    Tree to = studentRankingEachClass.current.next.tree;

                    if (studentRankingEachClass.current.next.id == -1) {
                        to = studentRankingEachClass.tail.prev.prev.tree;
                        studentRankingEachClass.tail.prev.prev.jumlah += studentRankingEachClass.current.jumlah;
                    } else {
                        studentRankingEachClass.current.next.jumlah += studentRankingEachClass.current.jumlah;
                    }

                    studentRankingEachClass.current.prev.next = studentRankingEachClass.current.next;
                    studentRankingEachClass.current = studentRankingEachClass.current.next;
                    studentRankingEachClass.current.prev = studentRankingEachClass.current.prev.prev;

                    if (studentRankingEachClass.current.id == -1) {
                        studentRankingEachClass.current = studentRankingEachClass.current.prev;
                    }
                    from.move(from.root, -1, 1, to);
                }
                out.println(id);

            } else {
                out.println("-1");
            }

        } else {
            out.println("-1");
        }

    }

    static boolean checkRanking() {
        int value = studentRankingEachClass.current.tree.root.hitungSubtree;
        return value < 6;
    }

    static boolean checkNode(Node node) {
        return node != null;
    }

    // taken from https://codeforces.com/submissions/Petr
    // together with PrintWriter, these input-output (IO) is much faster than the
    // usual Scanner(System.in) and System.out
    // please use these classes to avoid your fast algorithm gets Time Limit
    // Exceeded caused by slow input-output (IO)
    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }

    }
}

class Student {
    int id, poin;

    Student(int _id, int _poin) {
        this.id = _id;
        this.poin = _poin;
    }
}

class Node {
    int nilai, tinggi;
    int hitungNode, hitungSubtree;
    Tree idPohon;
    Node kiri, kanan;

    Node(int data) {
        inisialisasiNode(data);
    }

    void inisialisasiNode(int data) {
        nilai = data;
        tinggi = 1;
        hitungNode = 1;
        hitungSubtree = 1;
        idPohon = new Tree();
    }

    public String toString(){
        return "Node{" + "Value =" + nilai + "}";
    }
}

class Tree {
    Node root;

    int tinggi(Node N) {
        return (N == null) ? 0 : N.tinggi;
    }

    Node rightRotate(Node y) {
        return inisialisasiRightRotate(y);

    }

    int calculateTinggi(Node y) {
        return Math.max(tinggi(y.kiri), tinggi(y.kanan)) + 1;
    }

    Node inisialisasiRightRotate(Node y) {
        Node x = y.kiri;
        Node z = x.kanan;

        x.kanan = y;
        y.kiri = z;

        y.tinggi = calculateTinggi(y);
        x.tinggi = calculateTinggi(x);
        x = getRecount(x);
        return x;
    }

    Node leftRotate(Node x) {
        Node y = x.kanan;
        Node z = y.kiri;

        y.kiri = x;
        x.kanan = z;

        x.tinggi = calculateTinggi(x);
        y.tinggi = calculateTinggi(y);

        y = getRecount(y);
        return y;
    }

    int getBalance(Node node) {
        return (node == null) ? 0 : (tinggi(node.kiri) - tinggi(node.kanan));
    }

    Node getRecount(Node node) {
        int total = 0;
        if (node.kanan != null) {
            int totalRight = 0;
            if (node.kanan.kanan != null) {
                totalRight += node.kanan.kanan.hitungSubtree;
            }

            if (node.kanan.kiri != null) {
                totalRight += node.kanan.kiri.hitungSubtree;
            }
            node.kanan.hitungSubtree= totalRight + node.kanan.hitungNode;
            total += node.kanan.hitungSubtree;
        }

        if (node.kiri != null) {
            int totalLeft = 0;
            if (node.kiri.kanan != null) {
                totalLeft += node.kiri.kanan.hitungSubtree;
            }

            if (node.kiri.kiri != null) {
                totalLeft += node.kiri.kiri.hitungSubtree;
            }
            node.kiri.hitungSubtree = totalLeft + node.kiri.hitungNode;
            total += node.kiri.hitungSubtree;
        }

        node.hitungSubtree = total + node.hitungNode;
        return node;
    }

    Node insert(Node node, int id) {
        if (node == null) {
            Node newNode = new Node(id);
            return newNode;
        }

        if (id < node.nilai) {
            node.kiri = insert(node.kiri, id);
        } else if (id > node.nilai) {
            node.kanan = insert(node.kanan, id);
        } else {
            return node;
        }

        node.tinggi = calculateTinggi(node);
        return balanceNode(node, id);
    }

    Node insert(Node node, int poin, int id) {
        if (node == null) {
            return createNewNode(poin, id);
        }

        if (poin < node.nilai) {
            node.kiri = insert(node.kiri, poin, id);
        } else if (poin > node.nilai) {
            node.kanan = insert(node.kanan, poin, id);
        } else {
            node.idPohon.root = node.idPohon.insert(node.idPohon.root, id);
            node.hitungNode++;
            node = getRecount(node);
            return node;
        }

        node.tinggi = calculateTinggi(node);

        return getRecount(balanceNode(node, poin));
    }

    Node createNewNode(int poin, int id) {
        Node currNode = new Node(poin);
        currNode.idPohon.root = currNode.idPohon.insert(currNode.idPohon.root, id);
        return currNode;
    }

    Node balanceNode(Node node, int poin) {
        int balance = getBalance(node);

        if (balance > 1 && poin < node.kiri.nilai)
            return rightRotate(node);

        if (balance < -1 && poin > node.kanan.nilai)
            return leftRotate(node);

        if (balance > 1 && poin > node.kiri.nilai) {
            node.kiri = leftRotate(node.kiri);
            return rightRotate(node);
        }

        if (balance < -1 && poin < node.kanan.nilai) {
            node.kanan = rightRotate(node.kanan);
            return leftRotate(node);
        }
        return node;

    }

    Node getMinValueNode(Node node) {
        Node currentNode = node;
        while (currentNode.kiri != null) {
            currentNode = currentNode.kiri;
        }
        return currentNode;
    }

    // Print the tree
    public void printTree(Node currPtr, String indent, boolean last) {
        if (currPtr != null) {
        System.out.print(indent);
        if (last) {
            System.out.print("R----");
            indent += "   ";
        } else {
            System.out.print("L----");
            indent += "|  ";
        }
        System.out.println(currPtr.nilai);
        printTree(currPtr.kiri, indent, false);
        printTree(currPtr.kanan, indent, true);
        }
    }


    Student minValueNode(Node node, int parentValue, int depth) {
        Node currentNode = node;
        while (currentNode.kiri != null) {
            //System.out.println(node.kiri != null);
            currentNode = currentNode.kiri;
        }

        if (depth == 1) {
            return minValueNode(currentNode.idPohon.root, currentNode.nilai, depth - 1);
        }

        // Mengembalikan pasangan nilai: nilai dari node parent dan nilai dari node
        // dengan nilai minimum
        return new Student(parentValue, currentNode.nilai);
    }

    Student maxValueNode(Node node, int parentValue, int depth) {
        Node currentNode = node;

        while (currentNode.kanan != null) {
            //System.out.println(currentNode.kanan != null);
            currentNode = currentNode.kanan;
        }

        if (depth == 1) {
            return maxValueNode(currentNode.idPohon.root, currentNode.nilai, depth - 1);
        }

        return new Student(parentValue, currentNode.nilai);
    }

    Node delete(Node node, int id) {
        if (node == null)
            return node;

        if (id < node.nilai) {
            node.kiri = delete(node.kiri, id);
        } else if (id > node.nilai) {
            node.kanan = delete(node.kanan, id);
        } else {
            node = deleteNode(node);
            
        }


        if (node == null)
            return node;

        node.tinggi = calculateTinggi(node);
        int balance = getBalance(node);

        if (balance > 1) {
            if (getBalance(node.kiri) >= 0) {
                return rightRotate(node);
            } else {
                node.kiri = leftRotate(node.kiri);
                return rightRotate(node);
            }
        }

        if (balance < -1) {
            if (getBalance(node.kanan) <= 0)
                return leftRotate(node);
            else {
                node.kanan = rightRotate(node.kanan);
                return leftRotate(node);
            }
        }
        return node;
    }

    Node deleteNode(Node node) {
        if ((node.kiri == null) || (node.kanan == null)) {
            Node temp = null;
            if (temp == node.kiri)
                temp = node.kanan;
            else
                temp = node.kiri;

            if (temp == null) {
                temp = node;
                node = null;
            } else
                node = temp;
        } else {
            Node temp = getMinValueNode(node.kanan);
            node.nilai = temp.nilai;
            node.kanan = delete(node.kanan, temp.nilai);
        }

        return node;
    }

    Node delete(Node node, int poin, int id, int check) {
        if (node == null) return node;
        if (poin < node.nilai)
            node.kiri = delete(node.kiri, poin, id, check);
        else if (poin > node.nilai)
            node.kanan = delete(node.kanan, poin, id, check);
        else {
            if (node.hitungNode <= 1 || check == 1) {
                if ((node.kiri == null) || node.kanan == null) {
                    Node temp = null;
                    if (temp == node.kiri)
                        temp = node.kanan;
                    else
                        temp = node.kiri;
                    if (temp == null) {
                        temp = node;
                        node = null;
                    } else
                        node = temp;
                } else {
                    Node temp = getMinValueNode(node.kanan);
                    node.nilai = temp.nilai;
                    node.hitungNode = temp.hitungNode;
                    node.idPohon.root = temp.idPohon.root;

                    node.kanan = delete(node.kanan, temp.nilai, id, 1);
                    node = getRecount(node);
                }
            } else {
                node.hitungNode = node.hitungNode -1;
                node.hitungSubtree = node.hitungNode -1;
                node.idPohon.root = delete(node.idPohon.root, id);

            }
        }
        if (node == null)
            return node;

        node.tinggi = calculateTinggi(node);

        int balance = getBalance(node);

        if (balance > 1 && getBalance(node.kiri) >= 0) 
            return rightRotate(node);

        if(balance > 1 && getBalance(node.kiri) < 0) {
                node.kiri = leftRotate(node.kiri);
                return rightRotate(node);
            }
        
        if (balance < -1 && getBalance(node.kanan) <= 0) 
        
            return leftRotate(node);
        if(balance < -1 && getBalance(node.kanan)>0) {
            node.kanan = rightRotate(node.kanan);
            return leftRotate(node);
        }
        

        node = getRecount(node);
        return node;
    }
    
    Node findNode(Node node, int id) {
        if (node == null)
            return node;
        if (id < node.nilai)
            return findNode(node.kiri, id);
        else if (id > node.nilai)
            return findNode(node.kanan, id);
        else
            return node;
    }

    Node findNode(Node node, int poin, int id) {
        if (node == null)
            return node;
        if (poin < node.nilai)
            return findNode(node.kiri, poin, id);
        else if (poin > node.nilai)
            return findNode(node.kanan, poin, id);
        else
            return findNode(node.idPohon.root, id);
    }

    int amountOfTutor(Node node, int poin) {
        if (node == null)
            return 0;
        int add = 0;
        if (poin > node.nilai) {
            if (node.kiri != null)
                add += node.kiri.hitungSubtree;

            add += node.hitungNode;
            add += amountOfTutor(node.kanan, poin);
        } else if (poin < node.nilai)
            add += amountOfTutor(node.kiri, poin);
        else {
            add += node.hitungNode;
            if (node.kiri != null)
                add += node.kiri.hitungSubtree;
        }
        return add;
    }

    void move(Node node, int parentValue, int kedalaman, Tree to) {
        if (node == null)
            return;

        if (kedalaman > 0) {
            move(node.idPohon.root, node.nilai, kedalaman - 1, to);
        } else if (kedalaman == 0) {
            to.root = to.insert(to.root, parentValue, node.nilai);
        }

        move(node.kanan, parentValue, kedalaman, to);
        move(node.kiri, parentValue, kedalaman, to);
    }
}

class nodeForLinkedList {
    public int id;
    public int jumlah;
    public Tree tree;
    public nodeForLinkedList next;
    public nodeForLinkedList prev;

    public nodeForLinkedList(int id) {
        initialize(id);
    }

    void initialize(int id) {
        this.id = id;
        this.jumlah = 0;
        this.tree = new Tree();
    }

    public double calculateAverage() {
        double average = (double) jumlah / (double) tree.root.hitungSubtree;
        return average;
    }
}

class LinkedList {
    int size;
    public nodeForLinkedList head = new nodeForLinkedList(-1);
    public nodeForLinkedList tail = new nodeForLinkedList(-1);
    public nodeForLinkedList current = head;

    public nodeForLinkedList addBack(int id) {
        this.size = this.size + 1;
        if (this.size == 1) {
            nodeForLinkedList temp = new nodeForLinkedList(id);
            temp.prev = head;
            temp.next = tail;

            head.next = temp;
            tail.prev = temp;
            current = temp;
            return temp;
        } else {
            nodeForLinkedList temp = new nodeForLinkedList(id);
            temp.prev = tail.prev;
            temp.next = tail;
            tail.prev.next = temp;
            tail.prev = temp;
            return temp;

        }
    }

    public void insertionSort() {
        nodeForLinkedList currentNode = head.next.next;
        while (currentNode.id != -1) {
            nodeForLinkedList nextNode = currentNode.next;
            nodeForLinkedList beforeNode = findInsertPosition(currentNode);

            if (beforeNode.next.id == currentNode.id) {
                currentNode = nextNode;
                continue;
            }

            // Remove currentNode from its current position
            currentNode.prev.next = currentNode.next;
            currentNode.next.prev = currentNode.prev;

            // Insert currentNode after beforeNode
            currentNode.prev = beforeNode;
            currentNode.next = beforeNode.next;
            beforeNode.next.prev = currentNode;
            beforeNode.next = currentNode;

            currentNode = nextNode;
        }
    }

    private nodeForLinkedList findInsertPosition(nodeForLinkedList node) {
        nodeForLinkedList beforeNode = node.prev;
        while (beforeNode.id != -1 && shouldMoveBefore(node, beforeNode)) {
            beforeNode = beforeNode.prev;
        }
        return beforeNode;
    }

    private boolean shouldMoveBefore(nodeForLinkedList node, nodeForLinkedList beforeNode) {
        return beforeNode.calculateAverage() < node.calculateAverage() ||
                (beforeNode.calculateAverage() == node.calculateAverage() && beforeNode.id > node.id);
    }


    public void moveLeft() {
        current = (current == null) ? current : current.prev;
        current = (current.id == -1) ? tail.prev : current;
    }

    public void moveRight() {
        current = (current == null) ? current : current.next;
        current = (current.id == -1) ? head.next : current;
    }

}
