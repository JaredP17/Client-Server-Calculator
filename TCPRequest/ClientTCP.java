import java.net.*;  // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*;   // for IOException
import java.util.*; // for Scanner

public class ClientTCP {

  public static void main(String args[]) throws Exception {

      if (args.length != 2)  // Test for correct # of args
        throw new IllegalArgumentException("Parameter(s): <Destination> <Port>");

      InetAddress destAddr = InetAddress.getByName(args[0]);  // Destination address
      int destPort = Integer.parseInt(args[1]) + TCPRequestBinConst.GROUP_ID;               // Destination port

      byte id = 0;
      TCPRequestEncoder encoder = new TCPRequestEncoderBin();
      TCPRequestDecoder decoder = new TCPRequestDecoderBin();

      cleanUp();
      System.out.println("Running Calculator-server Client (TCP).");
      System.out.println("Destination Address: " + destAddr.getHostAddress());
      System.out.println("Destination Port: " + destPort);

      /*TCPRequest request = new TCPRequest((byte) 8, (byte) 1,
        (byte) 0, (byte) 2, (short) 3, (short) 4);

      System.out.println("Display request");
      System.out.println(request); // Display request just to check what we send
      */

      for (;;) {
        try {
          Scanner input = new Scanner(System.in);
          System.out.println("\n*** Request New Calculation ***\n"
                             + "0: Addition (+)\n"
                             + "1: Subtraction (-)\n"
                             + "2: Multiplication (*)\n"
                             + "3: Division (/)\n"
                             + "4: Shift Right (>>)\n"
                             + "5: Shift Left (<<)\n"
                             + "6: One-Complement (NOT)(~)\n");
          System.out.print("Enter Operation Code: ");
          String userInput = input.nextLine();
          byte opCode = Byte.parseByte(userInput);

          if (opCode < 0 || opCode > 6) {
            System.out.println("*** Error: Please enter a valid operation code. ***");
            proceed();
            cleanUp();
            continue;
          }

          System.out.print("Enter Operand 1: ");
          short op1 = input.nextShort();
          short op2 = 0; // Initialized to 0 in case opCode = 6
          byte numOperands = 1;

          if (opCode != 6) {
            System.out.print("Enter Operand 2: ");
            op2 = input.nextShort();
            numOperands = 2;
          }

          byte tml = 8;
          TCPRequest request = new TCPRequest(tml, id, opCode, numOperands,
              op1, op2);

          Socket sock = new Socket(destAddr, destPort);

          byte[] codedRequest = encoder.encode(request); // Encode request
          System.out.print("\nMessage sent in bytes:");
          for (byte hexValue : codedRequest) {
            System.out.print(" 0x" + String.format("%02x", hexValue).toUpperCase()); // Prints each byte of outgoing packet
          }
          System.out.println(); // New line

          OutputStream out = new DataOutputStream(sock.getOutputStream()); // Get a handle onto Output Stream
          out.write(codedRequest); // Encode and send
          long startTime = System.nanoTime(); // Time of request
          id++;

          // Get response from Server
          System.out.println("\nWaiting for response...");
          InputStream in = new BufferedInputStream(sock.getInputStream());
          long endTime = System.nanoTime(); // Time of Response
          long totalTime = (endTime - startTime);
          TCPResponse response = decoder.decodeRes(in);

          String message = "Message received in bytes:";
          byte [] codedResponse = encoder.encode(response);
          int i = 0;
          for (byte hexValue : codedResponse) {
            if (i < response.tml) {
              message += " 0x" + String.format("%02x", hexValue).toUpperCase(); // Prints each byte of outgoing packet
              i++;
            }
          }
          System.out.println(message); // New line
          System.out.println("\n" + response);

          // Print total time in milliseconds
          System.out.println("Response Time: " + (double) totalTime / 1000000 + " ms");
          // Prompt to continue for another request
          proceed();

          // Close socket and data streams
          in.close();
          out.close();
          sock.close();

          // Clears/Cleans-up console enviornment
          cleanUp();
        }

      catch (NumberFormatException e) {
        System.out.println("*** Error: Please enter valid input. ***");
        proceed();
        cleanUp();
        continue;
      }

      catch (Exception e) {
        System.out.println(e.getMessage());
        proceed();
        cleanUp();
        continue;
      }
    }
  }

  private static void proceed() {
    Scanner scan = new Scanner(System.in);
    System.out.print("\nType \"quit\" or hit enter to continue: ");
    String str = scan.nextLine();

    if (str.equals("quit")) {
      System.exit(0);
    }
  }

  private static void cleanUp() {
    // Clears/Cleans-up console enviornment
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }
}
