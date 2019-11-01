import java.io.*;  // for ByteArrayInputStream
import java.net.*; // for DatagramPacket

public class RequestDecoderBin implements RequestDecoder, RequestBinConst {

  private String encoding;  // Character encoding

  public RequestDecoderBin() {
    encoding = DEFAULT_ENCODING;
  }

  public RequestDecoderBin(String encoding) {
    this.encoding = encoding;
  }

  public Request decode(InputStream wire) throws IOException {
    DataInputStream src = new DataInputStream(wire);
    byte tml            = src.readByte();
    byte requestID      = src.readByte();
    byte opCode         = src.readByte();
    byte numOperands    = src.readByte();
    short op1           = src.readShort();
    short op2           = src.readShort();

    return new Request(tml, requestID, opCode, numOperands, op1, op2);
  }

  public Request decode(DatagramPacket p) throws IOException {
    ByteArrayInputStream payload =
      new ByteArrayInputStream(p.getData(), p.getOffset(), p.getLength());
    return decode(payload);
  }

  public Response decodeRes(InputStream wire) throws IOException {
    DataInputStream src = new DataInputStream(wire);
    byte tml            = src.readByte();
    byte requestID      = src.readByte();
    byte errorCode      = src.readByte();
    int result          = src.readInt();

    return new Response(tml, requestID, errorCode, result);
  }

  public Response decodeRes(DatagramPacket p) throws IOException {
    ByteArrayInputStream payload =
      new ByteArrayInputStream(p.getData(), p.getOffset(), p.getLength());
    return decodeRes(payload);
  }
}
