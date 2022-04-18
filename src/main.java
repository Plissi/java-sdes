
public class main {
    public static void main(String[] args) {
        String key = "1100100000";
        String message = "Hello world!";
        SDES sdes = new SDES(key);
        //String message = "ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 1234567890";
        String message_enc = "";
        String message_dec = "";

        for (int i = 0; i < message.length(); i++)
            message_enc += sdes.encrypt(message.charAt(i));

        System.out.println(message + " est le message à coder" + "\n");
        System.out.println("Le message codé est " + message_enc + "\n");

        for (int i = 0; i < message_enc.length(); i++)
            message_dec += sdes.decrypt(message_enc.charAt(i));

        System.out.println("Le message décodé est " + message_dec);

    }
}