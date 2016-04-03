import java.util.Scanner;

/**
 * Created by victorchoudhary on 15/05/15.
 */
public class cdht {
    
    private static int[] pingServerPort = new int[256];
    private final static int BASE_PORT = 50000;
    
    public static void main(String args[]){
        
        if (args.length == -1){
            System.out.println("Argument required : IDENTITY");
            return;
        }
        
        int identity = Integer.parseInt(args[0]);
        int successor1 = Integer.parseInt(args[1]);
        int successor2 = Integer.parseInt(args[2]);
        
        pingServerPort[identity] = BASE_PORT+identity;
        Thread ping = new PingServer(identity,successor1,successor2);
        ping.start();
        
    }
    
    
    // ------------------------------------------------------------------------------------------
    // UTILITY METHODS
    // ------------------------------------------------------------------------------------------
    
    public static int getPort(int identity){
        return BASE_PORT+identity;
    }
    
}
