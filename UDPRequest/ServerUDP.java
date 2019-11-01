import java.net.*;  // for DatagramSocket and DatagramPacket
import java.io.*;   // for IOException

public class ServerUDP {

  public static void main(String[] args) throws Exception {

      if (args.length != 1 && args.length != 2)  // Test for correct # of args
	  throw new IllegalArgumentException("Parameter(s): <Port> [<encoding>]");

      int port = Integer.parseInt(args[0]) + RequestBinConst.GROUP_ID;   // Receiving Port
      System.out.println("Calculating Server: Online\nPort: " + port);

      // Variables for generating response to client
      byte tml = 7;
      byte ID = 0;
      byte err = 0;
      int result = 0;

      for (;;) {
      System.out.println("Waiting for request...\n");
      DatagramSocket sock = new DatagramSocket(port);  // UDP socket for sending/receiving
      DatagramPacket packet = new DatagramPacket(new byte[1024],1024);
      sock.receive(packet);

      // Receive binary-encoded request
      RequestDecoder decoder = new RequestDecoderBin();
      Request receivedRequest = decoder.decode(packet);
      // System.out.println("Packet size: " + packet.getLength() + " bytes"); Print packet length for debugging purposes

      System.out.println("Received Binary-Encoded Request");
      System.out.println("Calculating...\n");
      System.out.println(receivedRequest);

      if (receivedRequest.tml == packet.getLength()) {
        err = 0;
      } else {
        err = 127;
      }

      switch (receivedRequest.opCode) {
        case 0:
          result = receivedRequest.op1 + receivedRequest.op2;
          break;
        case 1:
          result = receivedRequest.op1 - receivedRequest.op2;
          break;
        case 2:
          result = receivedRequest.op1 * receivedRequest.op2;
          break;
        case 3:
          result = receivedRequest.op1 / receivedRequest.op2;
          break;
        case 4:
          result = receivedRequest.op1 >> receivedRequest.op2;
          break;
        case 5:
          result = receivedRequest.op1 << receivedRequest.op2;
          break;
        case 6:
          result = ~receivedRequest.op1;
          break;
       default:
          break;
      }

      System.out.println("Result: " + result + "\n\nSending response to client: "
                      + packet.getAddress().getHostAddress() + "\n");

      // Encoded response to return to the client
      Response response = new Response(tml, receivedRequest.requestID, err, result);
      RequestEncoder encoder = new RequestEncoderBin();

      byte[] codedResponse = encoder.encode(response);

      DatagramPacket resPacket = new DatagramPacket(codedResponse, codedResponse.length,
						  packet.getAddress(), port); // Originally received packet contains return address

      sock.send(resPacket);

      // Close socket
      sock.close();

      // Reset length to avoid shrinking buffer
      packet.setLength(1024);
    }
  }
}
