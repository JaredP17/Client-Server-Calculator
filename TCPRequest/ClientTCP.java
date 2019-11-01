import java.net.*;  // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*;   // for IOException
import java.util.*; // for Scanner

public class ClientTCP {

  public static void main(String args[]) throws Exception {
    byte tml = 8;
    byte ID = 0;
    byte opCode = -1;
    byte numOperands = -1;
    short operand1 = 0;
    short operand2 = 0;

      if (args.length != 2)  // Test for correct # of args
        throw new IllegalArgumentException("Parameter(s): <Destination> <Port>");

      InetAddress destAddr = InetAddress.getByName(args[0]);  // Destination address
      int destPort = Integer.parseInt(args[1]) + TCPRequestBinConst.GROUP_ID;               // Destination port

      System.out.println("Running Calculator-server Client.");
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
        opCode = Byte.parseByte(userInput);

        if (opCode >= 0 && opCode <= 6) {
          if (opCode == 6) {
            numOperands = 1;
          } else {
            numOperands = 2;
          }
        }
        else {
          throw new Exception("*** Error: Please enter a valid operation code. ***"); // Throws exception if op code is out of range
        }

        if (numOperands == 1) {
          System.out.print("Enter Operand: ");
          operand1 = input.nextShort();
          operand2 = 0; // operand 2 is not used. Set to 0
        }
        else if (numOperands == 2) {

          System.out.print("Enter Operand 1: ");
          operand1 = input.nextShort();
          System.out.print("Enter Operand 2: ");
          operand2 = input.nextShort();
        }

      TCPRequest request = new TCPRequest(tml, ID, opCode, numOperands,
          operand1, operand2);

      Socket sock = new Socket(destAddr, destPort);

      // Use the encoding scheme given on the command line (args[2])
      TCPRequestEncoder encoder = new TCPRequestEncoderBin();

      byte[] codedRequest = encoder.encode(request); // Encode request
      System.out.print("\nMessage sent in bytes:");
      for (byte hexValue : codedRequest) {
        System.out.print(" 0x" + String.format("%02x", hexValue).toUpperCase()); // Prints each byte of outgoing packet
      }
      System.out.println(); // New line

      System.out.println("Sending TCPRequest (Binary)");
      OutputStream out = new DataOutputStream(sock.getOutputStream()); // Get a handle onto Output Stream
      out.write(codedRequest); // Encode and send
      long startTime = System.nanoTime(); // Time of request
      ID++;

      // Get response from Server
      System.out.println("Waiting for response...\n");
      InputStream in = new BufferedInputStream(sock.getInputStream());
      long endTime = System.nanoTime(); // Time of Response
      long totalTime = (endTime - startTime);
      TCPRequestDecoder decoder = new TCPRequestDecoderBin();
      TCPResponse receivedResponse = decoder.decodeRes(in);
      System.out.println("Received Binary-Encoded Response:");
      System.out.println(receivedResponse);

      // Print total time in milliseconds
      System.out.println("Response Time: " + (double) totalTime / 1000000 + " ms");
      // Prompt to continue for another request
      Scanner scan = new Scanner(System.in);
      System.out.print("Type \"quit\" or hit enter to continue: ");
      String str = scan.nextLine();

      if (str.equals("quit")) {
        System.exit(0);
      }

      // Close socket and data streams
      in.close();
      out.close();
      sock.close();

      // Reset length to avoid shrinking buffer
    //  packet.setLength(1024);

      // Clears/Cleans-up console enviornment
      System.out.print("\033[H\033[2J");
      System.out.flush();
    }

    catch (NumberFormatException e) {
      System.out.println("*** Error: Please enter valid input. ***");
      continue;
    }

    catch (Exception e) {
      System.out.println(e.getMessage());
      continue;
    }

      }
  }
}
