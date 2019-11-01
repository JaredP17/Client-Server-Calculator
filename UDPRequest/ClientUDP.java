import java.net.*;  // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*;   // for IOException
import java.util.*; // for Scanner

public class ClientUDP {

  public static void main(String args[]) throws Exception {
    byte tml = 8;
    byte ID = 0;
    byte opCode = -1;
    byte numOperands = -1;
    short operand1 = 0;
    short operand2 = 0;

      if (args.length != 1 && args.length != 2)  // Test for correct # of args
	  throw new IllegalArgumentException("Parameter(s): <Destination>" +
					     " <Port> [<encoding]");


      InetAddress destAddr = InetAddress.getByName(args[0]);  // Destination address
      int destPort = Integer.parseInt(args[1]) + RequestBinConst.GROUP_ID; // Destination port

      System.out.println("Running Calculator-server Client.");
      System.out.println("Destination Address: " + destAddr.getHostAddress());
      System.out.println("Destination Port: " + destPort);

      Scanner input = new Scanner(System.in);
      /*Request request = new Request((byte) 8, (byte) 1,
        (byte) 0, (byte) 2, (short) 3, (short) 4);*/

      for (;;) {
        System.out.println("\n*** Request New Calculation ***\n"
                           + "0: Addition (+)\n"
                           + "1: Subtraction (-)\n"
                           + "2: Multiplication (*)\n"
                           + "3: Division (/)\n"
                           + "4: Shift Right (>>)\n"
                           + "5: Shift Left (<<)\n"
                           + "6: One-Complement (NOT)(~)\n");
        System.out.print("Enter Operation Code: ");
        opCode = input.nextByte();

        if (opCode >= 0 && opCode <= 6) {
          if (opCode == 6) {
            numOperands = 1;
          } else {
            numOperands = 2;
          }
        }
        else {
          System.out.println("*** Error: Please enter a valid operation code. ***");
          continue;
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

      Request request = new Request(tml, ID, opCode, numOperands,
          operand1, operand2);

      DatagramSocket sock = new DatagramSocket(destPort);  // UDP socket for sending/receiving
      DatagramPacket packet = new DatagramPacket(new byte[1024],1024);

      // Use the encoding scheme given on the command line (args[2])
      RequestEncoder encoder = new RequestEncoderBin();

      byte[] codedRequest = encoder.encode(request); // Encode request
      System.out.print("\nMessage sent in bytes:");
      for (byte hexValue : codedRequest) {
        System.out.print(" 0x" + String.format("%02x", hexValue).toUpperCase()); // Prints each byte of outgoing packet
      }
      System.out.println(); // New line

      DatagramPacket message = new DatagramPacket(codedRequest, codedRequest.length,
						  destAddr, destPort);
      sock.send(message);
      long startTime = System.nanoTime(); // Time of request
      ID++;

      // Get response from Server
      System.out.println("Waiting for response...\n");
      sock.receive(packet);
      long endTime = System.nanoTime(); // Time of Response
      long totalTime = (endTime - startTime);
      RequestDecoder decoder = new RequestDecoderBin();
      Response receivedResponse = decoder.decodeRes(packet);
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

      // Close socket
      sock.close();

      // Reset length to avoid shrinking buffer
      packet.setLength(1024);

      // Clears/Cleans-up console enviornment
      System.out.print("\033[H\033[2J");
      System.out.flush();
    }
  }
}
