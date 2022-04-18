import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class SDES {
    private boolean[] master_key;
    private boolean[][][] s_box1 = {{{false, true}, {false, false}, {true, true}, {true, false}}, {{true, true}, {true, false}, {false, true}, {false, false}}, {{false, false}, {true, false}, {false, true}, {true, true}}, {{true, true}, {false, true}, {true, true}, {true, false}}};
    private boolean[][][] s_box2 = {{{false, false}, {false, true}, {true, false}, {true, true}}, {{true, false}, {false, false}, {false, true}, {true, true}}, {{true, true}, {false, false}, {false, true}, {false, false}}, {{true, false}, {false, true}, {false, false}, {true, true}}};

    public SDES(String _key) {
        master_key = new boolean[10];
        //convertir la chaîne en tableau de booléens
        for (int i = 0; i < _key.length(); i++) {
            master_key[i] = _key.charAt(i) == '1';
        }

    }

    // fonctions secondaires/ d'assistance
    boolean[] mergeArray(boolean[] arr1, boolean[] arr2) {
        //longueur des deux tableaux
        int l1 = arr1.length;
        int l2 = arr2.length;

        boolean[] result = new boolean[l1 + l2]; //tableau final

        System.arraycopy(arr1, 0, result, 0, l1); //copie du premier tableau dans le tableau de destination
        System.arraycopy(arr2, 0, result, l1, l2); //copie du deuxieme tableau dans le tableau de destination

        return result;
    }

    boolean[] permutation(boolean[] key, int[] positions) {
        boolean[] result = new boolean[positions.length];

        for (int i = 0; i < positions.length; i++) {
            result[i] = key[positions[i] - 1];
        }

        return result;
    }

    ArrayList<boolean[]> splitArrayInTwo(boolean[] array) {
        int keyLength = array.length;
        int midpoint = Math.round(keyLength / 2);

        boolean[] A = Arrays.copyOfRange(array, 0, midpoint);
        boolean[] B = Arrays.copyOfRange(array, midpoint, keyLength);

        ArrayList<boolean[]> result = new ArrayList<boolean[]>();
        result.add(A);
        result.add(B);

        return result;
    }

    boolean[] box(boolean[] p, boolean[][][] s_box) {
        int[] binaryP = new int[p.length]; // transformer l'entree en binaire pour facilement retrouver la position
        int[] position = new int[2];

        for (int i = 0; i < p.length; i++) {
            if (p[i]) {
                binaryP[i] = 1;
            } else {
                binaryP[i] = 0;
            }
        }

        position[0] = (int) (binaryP[0] * Math.pow(2, 1) + binaryP[3] * Math.pow(2, 0)); // position de la ligne avec p00 et p03 (binaryP[0] et binaryP[3])
        position[1] = (int) (binaryP[1] * Math.pow(2, 1) + binaryP[2] * Math.pow(2, 0)); // position de la colonne avec p01 et p02 (binaryP[1] et binaryP[2])

        return s_box[position[0]][position[1]];
    }

    int[] booleanToInt(boolean[] array) {
        int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i]) {
                result[i] = 1;
            } else {
                result[i] = 0;
            }
        }
        return result;
    }

    boolean[] binaryToBoolean(char[] array) {
        boolean[] result;
        boolean[] zeros;
        boolean[] temp = new boolean[array.length];

        for (int i = 0; i < array.length; i++) {
            if (array[i] == '1') {
                temp[i] = true;
            } else {
                temp[i] = false;
            }
        }

        if (array.length < 8) {
            zeros = new boolean[8 - array.length];
            return mergeArray(zeros, temp);
        } else {
            return temp;
        }
    }

    String intArrayToString(int[] array) {
        StringBuilder result = new StringBuilder();

        for (int j : array) {
            result.append(j);
        }

        return result.toString();
    }

    //fonctions principales
    boolean[] p10(boolean[] key) {
        //P10 (k1, k2, k3, k4, k5, k6, k7, k8, k9, k10) = (k3, k5, k2, k7, k4, k10, k1, k9, k8, k6)

        int[] positions = {3, 5, 2, 7, 4, 10, 1, 9, 8, 6};

        return permutation(key, positions);
    }

    boolean[] p8(boolean[] key) {
        //P8 (k1, k2, k3, k4, k5, k6, k7, k8, k9, k10) = (k6, k3, k7, k4, k8, k5, k10, k9)

        int[] positions = {6, 3, 7, 4, 8, 5, 10, 9};

        return permutation(key, positions);
    }

    boolean[] circularLeftShift(boolean[] key, int bits) {
        boolean[] A = splitArrayInTwo(key).get(0);
        ;
        boolean[] B = splitArrayInTwo(key).get(1);
        ;

        boolean[] AShift = new boolean[A.length];
        System.arraycopy(A, bits, AShift, 0, A.length - bits);
        System.arraycopy(A, 0, AShift, A.length - bits, bits);

        boolean[] BShift = new boolean[B.length];
        System.arraycopy(B, bits, BShift, 0, B.length - bits);
        System.arraycopy(B, 0, BShift, B.length - bits, bits);

        return mergeArray(AShift, BShift);
    }

    ArrayList<boolean[]> generateKeys() {
        boolean[] key = p10(master_key);
        boolean[] K1 = circularLeftShift(key, 1);
        boolean[] K2 = circularLeftShift(key, 3);

        K1 = p8(K1);
        K2 = p8(K2);

        ArrayList<boolean[]> result = new ArrayList<boolean[]>();
        result.add(K1);
        result.add(K2);

        return result;
    }

    boolean[] ip(boolean[] plainText) {
        //IP (k1, k2, k3, k4, k5, k6, k7, k8) = (k2, k6, k3, k1, k4, k8, k5, k7)
        int[] positions = {2, 6, 3, 1, 4, 8, 5, 7};
        return permutation(plainText, positions);
    }

    boolean[] rip(boolean[] permutedText) {
        //IP−1 (k1, k2, k3, k4, k5, k6, k7, k8) = (k4, k1, k3, k5, k7, k2, k8, k6)
        int[] positions = {4, 1, 3, 5, 7, 2, 8, 6};
        return permutation(permutedText, positions);
    }

    boolean[] ep(boolean[] input) {
        //E/P (n1, n2, n3, n4) = (n4, n1, n2, n3, n2, n3, n4, n1)
        int[] positions = {4, 1, 2, 3, 2, 3, 4, 1};
        return permutation(input, positions);
    }

    boolean[] xor(boolean[] a, boolean[] b) {
        int l = a.length;
        boolean[] result = new boolean[l];

        if (b.length == l) {
            for (int i = 0; i < l; i++) {
                boolean A = a[i];
                boolean B = b[i];
                result[i] = (A && !B) || (!A && B); // A XOR B <=> (A AND !B) OR (!A AND B)
            }
        }

        return result;
    }

    ArrayList<boolean[]> s_box_operation(boolean[] xor_result) {
        ArrayList<boolean[]> result = new ArrayList<>();

        boolean[] part1 = box(splitArrayInTwo(xor_result).get(0), s_box1);
        boolean[] part2 = box(splitArrayInTwo(xor_result).get(1), s_box2);

        result.add(part1);
        result.add(part2);

        return result;
    }

    boolean[] p4(boolean[] part1, boolean[] part2) {
        //P4 (s00, s01, s10, s11) = (s01, s11, s10, s00)
        boolean[] s_box_result = mergeArray(part1, part2);
        int[] positions = {2, 4, 3, 1};

        return permutation(s_box_result, positions);
    }

    boolean[] f(boolean[] right, boolean[] sk) {
        //on applique E/P sur right
        //on effectue un OU exclusif entre le résultat obtenu et la sous -clé sk passée en paramètre
        //on effectue les opérations des sand - boxes sur chaque moitie obtenue
        //on applique P4 sur le résultat et on le renvoie

        boolean[] ep = ep(right);
        ArrayList<boolean[]> parts = s_box_operation(xor(ep, sk));
        return p4(parts.get(0), parts.get(1));
    }

    boolean[] fK(boolean[] bits, boolean[] key) {
        //on effectue un OU exclusif entre les 4 bits de gauche en entrée et le résultat de la fonction F appliquée aux 4 bits de droite en entrée et a la clé passée en paramètre.
        //on concatène les 4 bits de droite en entrée avec le résultat précédemment obtenu, et on renvoie.
        boolean[] left = splitArrayInTwo(bits).get(0);
        boolean[] right = splitArrayInTwo(bits).get(1);

        return mergeArray(xor(left, f(right, key)), right);
    }

    boolean[] sw(boolean[] input) {
        // Intervertir les 4bits de gauche avec ceux de droite
        boolean[] part1 = splitArrayInTwo(input).get(1);
        boolean[] part2 = splitArrayInTwo(input).get(0);

        return mergeArray(part1, part2);
    }

    public char encrypt(char block) {
        //on génère les sous - cles K1 et K2
        boolean[] K1 = generateKeys().get(0);
        boolean[] K2 = generateKeys().get(1);

        //on applique IP
        char[] inputString = Integer.toBinaryString(block).toCharArray();
        boolean[] input = binaryToBoolean(inputString);

        //on applique fK avec K1
        //on applique SW
        //on applique fK avec K2
        //on applique IP^-1
        boolean[] encodedBoolean = rip(fK(sw(fK(ip(input), K1)), K2));

        //converir en ascii pour avoir le caractere correspondant
        String encodedBinary = intArrayToString(booleanToInt(encodedBoolean));
        int asciiCode = Integer.parseInt(encodedBinary, 2);
        //System.out.println("code " + asciiCode);
        //System.out.println("char " +(char) asciiCode);

        return (char) asciiCode;
    }

    public char decrypt(char block) {
        //on génère les sous - cles K1 et K2
        boolean[] K1 = generateKeys().get(0);
        boolean[] K2 = generateKeys().get(1);

        //on applique IP
        char[] inputString = Integer.toBinaryString(block).toCharArray();
        boolean[] input = binaryToBoolean(inputString);

        //on applique fK avec K2
        //on applique SW
        //on applique fK avec K1
        //on applique IP^-1
        boolean[] encodedBoolean = rip(fK(sw(fK(ip(input), K2)), K1));

        //converir en ascii pour avoir le caractere correspondant
        String encodedBinary = intArrayToString(booleanToInt(encodedBoolean));
        int asciiCode = Integer.parseInt(encodedBinary, 2);

        return (char) asciiCode;
    }

    public static void main(String[] args) {
        String part1 = "11001";
        String message = "Hello world!";

        System.out.println(message + " est le message à coder" + "\n");

        for (int j = 0; j < 32; j++) {
            String part2 = Integer.toBinaryString(j);
            if (part2.length()<5){
                String temp = "";
                for (int i = 0; i < 5-part2.length(); i++) {
                    temp += "0";
                }
                part2 = temp + part2;
            }
            String key = part1 + part2;

            System.out.println(j + ". La clé " + key);

            SDES sdes = new SDES(key);

            String message_enc = "";
            String message_dec = "";

            for (int i = 0; i < message.length(); i++)
                message_enc += sdes.encrypt(message.charAt(i));

            System.out.println(j + ". Le message codé est " + message_enc);

            for (int i = 0; i < message_enc.length(); i++)
                message_dec += sdes.decrypt(message_enc.charAt(i));

            System.out.println(j + ". Le message décodé est " + message_dec + "\n");
        }
    }
}

