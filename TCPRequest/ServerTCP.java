import java.net.*;  // for DatagramSocket and DatagramPacket
import java.io.*;   // for IOException

public class ServerTCP {

  public static void main(String args[]) throws Exception {

    if (args.length != 1)  // Test for correct # of args
      throw new IllegalArgumentException("Parameter(s): <Port>");

    int port = Integer.parseInt(args[0]) + TCPRequestBinConst.GROUP_ID;   // Receiving Port
    System.out.println("Calculating Server: Online\nPort: " + port);

    // Variables for generating response to client
    byte tml = 7;
    byte ID = 0;
    byte err = 0;
    int result = 0;



    for (;;) {
      // Create a server socket to accept client connection requests
      ServerSocket servSock = new ServerSocket(port);
      Socket clntSock = servSock.accept(); // Get client connection

      System.out.println("Waiting for request...\n");

      InputStream in = new BufferedInputStream(clntSock.getInputStream());

      // Receive binary-encoded request
      TCPRequestDecoder decoder = new TCPRequestDecoderBin();
      TCPRequest receivedRequest = decoder.decode(in);

      System.out.println("Received Binary-Encoded Request");
      System.out.println("Calculating...\n");
      System.out.println(receivedRequest);

      if (receivedRequest.tml == 8) {
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
                        + clntSock.getInetAddress().getHostAddress() + "\n");

      // Encoded response to return to the client
      TCPResponse response = new TCPResponse(tml, receivedRequest.requestID, err, result);
      TCPRequestEncoder encoder = new TCPRequestEncoderBin();

      byte[] codedResponse = encoder.encode(response);
      OutputStream out = new DataOutputStream(clntSock.getOutputStream()); // Get a handle onto Output Stream
      out.write(codedResponse); // Encode and send

      clntSock.close();
      servSock.close();

    }
  }
}
